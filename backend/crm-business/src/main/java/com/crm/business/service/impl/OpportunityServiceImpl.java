package com.crm.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.business.entity.Opportunity;
import com.crm.business.mapper.OpportunityMapper;
import com.crm.business.service.IOpportunityService;
import com.crm.business.vo.FunnelVO;
import com.crm.business.vo.OpportunityPageDTO;
import com.crm.common.exception.BusinessException;
import com.crm.system.service.DataPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商机服务实现
 *
 * @author CRM
 */
@Service
public class OpportunityServiceImpl extends ServiceImpl<OpportunityMapper, Opportunity> implements IOpportunityService {

    /** 商机阶段名称（1需求确认 2方案报价 3商务谈判 4合同签订 5已赢单 6已输单） */
    private static final String[] STAGE_NAMES = {null, "需求确认", "方案报价", "商务谈判", "合同签订", "已赢单", "已输单"};

    @Autowired
    private DataPermissionService dataPermissionService;

    /**
     * 分页查询商机
     */
    @Override
    public IPage<Opportunity> page(OpportunityPageDTO dto) {
        Page<Opportunity> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Opportunity> wrapper = new LambdaQueryWrapper<Opportunity>()
                .like(dto.getOppName() != null && !dto.getOppName().isEmpty(),
                        Opportunity::getOppName, dto.getOppName())
                .eq(dto.getCustomerId() != null,
                        Opportunity::getCustomerId, dto.getCustomerId())
                .eq(dto.getStage() != null,
                        Opportunity::getStage, dto.getStage())
                .eq(dto.getOwnerId() != null,
                        Opportunity::getOwnerId, dto.getOwnerId())
                .orderByDesc(Opportunity::getCreateTime);
        // 数据权限过滤
        List<Long> visibleOwnerIds = dataPermissionService.getVisibleOwnerIds();
        if (visibleOwnerIds != null) {
            wrapper.in(Opportunity::getOwnerId, visibleOwnerIds);
        }
        return baseMapper.selectPage(page, wrapper);
    }

    /**
     * 商机详情
     */
    @Override
    public Opportunity getById(Long id) {
        Opportunity opp = baseMapper.selectById(id);
        if (opp == null) {
            throw new BusinessException("商机不存在");
        }
        return opp;
    }

    /**
     * 新增商机
     */
    @Override
    public boolean addOpportunity(Opportunity opp) {
        if (opp.getExpectedCloseDate() != null && opp.getExpectedCloseDate().isBefore(LocalDate.now())) {
            throw new BusinessException("预计成交日期不能早于今天");
        }
        opp.setStageChangeTime(LocalDateTime.now());
        return baseMapper.insert(opp) > 0;
    }

    /**
     * 修改商机（阶段变更时同步更新阶段变更时间）
     */
    @Override
    public boolean updateOpportunity(Opportunity opp) {
        if (opp.getId() == null) {
            throw new BusinessException("商机ID不能为空");
        }
        // 阶段变更时更新阶段变更时间
        if (opp.getStage() != null) {
            Opportunity existing = baseMapper.selectById(opp.getId());
            if (existing != null && !opp.getStage().equals(existing.getStage())) {
                opp.setStageChangeTime(LocalDateTime.now());
            }
        }
        return baseMapper.updateById(opp) > 0;
    }

    /**
     * 删除商机（逻辑删除）
     */
    @Override
    public boolean deleteOpportunity(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    /**
     * 修改商机阶段
     * 已赢单(5)/已输单(6)为终态，不可再变更
     */
    @Override
    public boolean changeStage(Long id, Integer stage) {
        Opportunity opp = baseMapper.selectById(id);
        if (opp == null) {
            throw new BusinessException("商机不存在");
        }
        if (opp.getStage() != null && (opp.getStage() == 5 || opp.getStage() == 6)) {
            throw new BusinessException("该商机已结束（赢单/输单），无法变更阶段");
        }
        Opportunity update = new Opportunity();
        update.setId(id);
        update.setStage(stage);
        update.setStageChangeTime(LocalDateTime.now());
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 销售漏斗数据（按阶段统计数量和金额）
     */
    @Override
    public List<FunnelVO> getFunnelData() {
        // 查询全部商机
        List<Opportunity> list = baseMapper.selectList(null);

        // 按阶段分组统计数量与金额
        Map<Integer, List<Opportunity>> grouped = list.stream()
                .collect(Collectors.groupingBy(o -> o.getStage() == null ? 0 : o.getStage()));

        // 首阶段（需求确认）数量作为转化率基准
        int firstStageCount = grouped.getOrDefault(1, List.of()).size();

        // 遍历阶段 1-5 生成漏斗（不含已输单）
        return Arrays.stream(STAGE_NAMES)
                .skip(1)
                .limit(5)
                .map(stageName -> {
                    int stage = Arrays.asList(STAGE_NAMES).indexOf(stageName);
                    List<Opportunity> stageList = grouped.getOrDefault(stage, List.of());
                    FunnelVO vo = new FunnelVO();
                    vo.setStage(stage);
                    vo.setStageName(stageName);
                    vo.setCount(stageList.size());
                    BigDecimal totalAmount = stageList.stream()
                            .map(Opportunity::getEstimatedAmount)
                            .filter(a -> a != null)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    vo.setTotalAmount(totalAmount);
                    if (firstStageCount > 0) {
                        vo.setConversionRate(BigDecimal.valueOf(stageList.size())
                                .divide(BigDecimal.valueOf(firstStageCount), 4, RoundingMode.HALF_UP));
                    } else {
                        vo.setConversionRate(BigDecimal.ZERO);
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 查询停滞预警商机
     * 进行中（阶段1-4）且阶段变更时间超过指定天数的商机
     */
    @Override
    public List<Opportunity> listStagnant(int days) {
        LocalDateTime deadline = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<Opportunity> wrapper = new LambdaQueryWrapper<Opportunity>()
                // 进行中的商机阶段：1需求确认 2方案报价 3商务谈判 4合同签订
                .in(Opportunity::getStage, 1, 2, 3, 4)
                .lt(Opportunity::getStageChangeTime, deadline)
                .orderByAsc(Opportunity::getStageChangeTime);
        return baseMapper.selectList(wrapper);
    }
}
