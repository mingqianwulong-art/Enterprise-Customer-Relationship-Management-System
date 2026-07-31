package com.crm.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crm.market.entity.Clue;
import com.crm.market.mapper.ClueMapper;
import com.crm.market.service.ClueAssignRuleService;
import com.crm.market.service.IClueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 线索自动分配规则引擎实现
 * <p>
 * 分配规则（按优先级）：
 * 1. 区域+行业精确匹配
 * 2. 区域匹配
 * 3. 负载均衡（线索最少的活跃销售）
 *
 * @author CRM
 */
@Service
public class ClueAssignRuleServiceImpl implements ClueAssignRuleService {

    private static final Logger log = LoggerFactory.getLogger(ClueAssignRuleServiceImpl.class);

    @Autowired
    private ClueMapper clueMapper;

    @Autowired
    private IClueService clueService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 自动分配线索
     */
    @Override
    public Long autoAssign(Long clueId) {
        Clue clue = clueMapper.selectById(clueId);
        if (clue == null) {
            log.warn("[线索分配] 线索不存在 clueId={}", clueId);
            return null;
        }
        if (clue.getStatus() != null && clue.getStatus() != 0) {
            log.warn("[线索分配] 线索已分配 clueId={} status={}", clueId, clue.getStatus());
            return null;
        }

        Long userId = findBestSales(clue);
        if (userId == null) {
            log.warn("[线索分配] 无可用销售 clueId={}", clueId);
            return null;
        }

        // 执行分配
        boolean success = clueService.assignClue(clueId, userId);
        if (success) {
            log.info("[线索分配] 分配成功 clueId={} userId={}", clueId, userId);
            return userId;
        }
        log.error("[线索分配] 分配失败 clueId={} userId={}", clueId, userId);
        return null;
    }

    /**
     * 自动分配并发送通知（异步执行，1分钟内推送）
     */
    @Async
    @Override
    public boolean autoAssignAndNotify(Clue clue) {
        Long userId = autoAssign(clue.getId());
        if (userId == null) {
            return false;
        }
        // 发送消息通知（复用 sys_message 表）
        try {
            String title = "新线索分配通知";
            String content = String.format("您有一 条新线索需要跟进：%s（来源：%s）",
                    clue.getClueName(), clue.getSource());
            jdbcTemplate.update(
                    "INSERT INTO sys_message(user_id, title, content, type, ref_id, ref_type, is_read, create_time) " +
                    "VALUES(?, ?, ?, 3, ?, 'clue', 0, NOW())",
                    userId, title, content, clue.getId()
            );
            log.info("[线索分配] 通知已发送 userId={} clueId={}", userId, clue.getId());
        } catch (Exception e) {
            log.error("[线索分配] 通知发送失败 userId={} clueId={}", userId, clue.getId(), e);
        }
        return true;
    }

    /**
     * 批量自动分配所有待分配线索
     */
    @Override
    public int batchAutoAssign() {
        // 查询所有待分配线索
        LambdaQueryWrapper<Clue> wrapper = new LambdaQueryWrapper<Clue>()
                .eq(Clue::getStatus, 0)
                .orderByAsc(Clue::getCreateTime);
        List<Clue> pendingClues = clueMapper.selectList(wrapper);

        int count = 0;
        for (Clue clue : pendingClues) {
            Long userId = autoAssign(clue.getId());
            if (userId != null) {
                count++;
                // 发送通知
                try {
                    String title = "新线索分配通知";
                    String content = String.format("您有一条新线索需要跟进：%s（来源：%s）",
                            clue.getClueName(), clue.getSource());
                    jdbcTemplate.update(
                            "INSERT INTO sys_message(user_id, title, content, type, ref_id, ref_type, is_read, create_time) " +
                            "VALUES(?, ?, ?, 3, ?, 'clue', 0, NOW())",
                            userId, title, content, clue.getId()
                    );
                } catch (Exception e) {
                    log.error("[线索分配] 批量通知发送失败 clueId={}", clue.getId(), e);
                }
            }
        }
        log.info("[线索分配] 批量分配完成 总计={} 成功={}", pendingClues.size(), count);
        return count;
    }

    /**
     * 查找最佳销售
     */
    private Long findBestSales(Clue clue) {
        // 规则1：区域+行业精确匹配
        if (clue.getRegion() != null && !clue.getRegion().isEmpty()
                && clue.getIndustry() != null && !clue.getIndustry().isEmpty()) {
            Long userId = matchSalesByRegionAndIndustry(clue.getRegion(), clue.getIndustry());
            if (userId != null) {
                return userId;
            }
        }

        // 规则2：区域匹配
        if (clue.getRegion() != null && !clue.getRegion().isEmpty()) {
            Long userId = matchSalesByRegion(clue.getRegion());
            if (userId != null) {
                return userId;
            }
        }

        // 规则3：负载均衡
        return findLeastBusySales();
    }

    /**
     * 规则1：区域+行业精确匹配
     * <p>
     * 查找负责该区域且有同行业客户最多的销售
     */
    private Long matchSalesByRegionAndIndustry(String region, String industry) {
        try {
            // 查找负责该区域客户的销售，按匹配数量降序取第一个
            String sql = "SELECT c.owner_id AS userId, COUNT(*) AS matchCount " +
                    "FROM cus_customer c " +
                    "JOIN sys_user u ON u.id = c.owner_id AND u.deleted = 0 AND u.status = 1 " +
                    "WHERE c.deleted = 0 AND c.in_pool = 0 " +
                    "AND c.region = ? AND c.industry = ? " +
                    "AND c.owner_id IS NOT NULL " +
                    "GROUP BY c.owner_id " +
                    "ORDER BY matchCount DESC " +
                    "LIMIT 1";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, region, industry);
            if (!list.isEmpty()) {
                return ((Number) list.get(0).get("userId")).longValue();
            }
        } catch (Exception e) {
            log.error("[线索分配] 区域+行业匹配失败 region={} industry={}", region, industry, e);
        }
        return null;
    }

    /**
     * 规则2：区域匹配
     * <p>
     * 查找负责该区域客户最多的销售
     */
    private Long matchSalesByRegion(String region) {
        try {
            String sql = "SELECT c.owner_id AS userId, COUNT(*) AS matchCount " +
                    "FROM cus_customer c " +
                    "JOIN sys_user u ON u.id = c.owner_id AND u.deleted = 0 AND u.status = 1 " +
                    "WHERE c.deleted = 0 AND c.in_pool = 0 " +
                    "AND c.region = ? " +
                    "AND c.owner_id IS NOT NULL " +
                    "GROUP BY c.owner_id " +
                    "ORDER BY matchCount DESC " +
                    "LIMIT 1";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, region);
            if (!list.isEmpty()) {
                return ((Number) list.get(0).get("userId")).longValue();
            }
        } catch (Exception e) {
            log.error("[线索分配] 区域匹配失败 region={}", region, e);
        }
        return null;
    }

    /**
     * 规则3：负载均衡
     * <p>
     * 分配给当前待跟进线索最少的活跃销售
     */
    private Long findLeastBusySales() {
        try {
            // 查询角色为"销售"的活跃用户，按未完成线索数升序取第一个
            String sql = "SELECT u.id AS userId, " +
                    "COUNT(DISTINCT cl.id) AS pendingClues " +
                    "FROM sys_user u " +
                    "LEFT JOIN sys_user_role ur ON ur.user_id = u.id " +
                    "LEFT JOIN sys_role r ON r.id = ur.role_id AND r.deleted = 0 " +
                    "LEFT JOIN market_clue cl ON cl.owner_id = u.id AND cl.deleted = 0 AND cl.status = 1 " +
                    "WHERE u.deleted = 0 AND u.status = 1 " +
                    "AND (r.role_key = 'sale' OR r.role_key = 'sales')" +
                    "GROUP BY u.id " +
                    "ORDER BY pendingClues ASC " +
                    "LIMIT 1";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            if (!list.isEmpty()) {
                return ((Number) list.get(0).get("userId")).longValue();
            }

            // 兜底：查询所有活跃用户中线索最少的
            String fallbackSql = "SELECT u.id AS userId, " +
                    "COUNT(DISTINCT cl.id) AS pendingClues " +
                    "FROM sys_user u " +
                    "LEFT JOIN market_clue cl ON cl.owner_id = u.id AND cl.deleted = 0 AND cl.status = 1 " +
                    "WHERE u.deleted = 0 AND u.status = 1 " +
                    "GROUP BY u.id " +
                    "ORDER BY pendingClues ASC " +
                    "LIMIT 1";
            List<Map<String, Object>> fallbackList = jdbcTemplate.queryForList(fallbackSql);
            if (!fallbackList.isEmpty()) {
                return ((Number) fallbackList.get(0).get("userId")).longValue();
            }
        } catch (Exception e) {
            log.error("[线索分配] 负载均衡查询失败", e);
        }
        return null;
    }
}
