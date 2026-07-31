package com.crm.report.mapper;

import com.crm.report.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 报表 Mapper —— 跨模块聚合查询
 */
@Mapper
public interface ReportMapper {

    // ==================== 概览统计 ====================

    /** 客户总数 */
    @Select("SELECT COUNT(*) FROM cus_customer WHERE deleted = 0")
    Long countCustomers();

    /** 本月新增客户 */
    @Select("SELECT COUNT(*) FROM cus_customer WHERE deleted = 0 AND DATE_FORMAT(create_time, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')")
    Long countMonthlyNewCustomers();

    /** 公海客户数 */
    @Select("SELECT COUNT(*) FROM cus_customer WHERE deleted = 0 AND in_pool = 1")
    Long countPoolCustomers();

    /** 线索总数 */
    @Select("SELECT COUNT(*) FROM market_clue WHERE deleted = 0")
    Long countClues();

    /** 已转化线索数 */
    @Select("SELECT COUNT(*) FROM market_clue WHERE deleted = 0 AND status = 2")
    Long countConvertedClues();

    /** 商机总数 */
    @Select("SELECT COUNT(*) FROM bus_opportunity WHERE deleted = 0")
    Long countOpportunities();

    /** 进行中商机数（阶段1-4） */
    @Select("SELECT COUNT(*) FROM bus_opportunity WHERE deleted = 0 AND stage BETWEEN 1 AND 4")
    Long countActiveOpportunities();

    /** 已赢单商机数 */
    @Select("SELECT COUNT(*) FROM bus_opportunity WHERE deleted = 0 AND stage = 5")
    Long countWonOpportunities();

    /** 合同总数 */
    @Select("SELECT COUNT(*) FROM bus_contract WHERE deleted = 0")
    Long countContracts();

    /** 合同总金额 */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM bus_contract WHERE deleted = 0")
    java.math.BigDecimal sumContractAmount();

    /** 已回款金额（status=2 已回款） */
    @Select("SELECT COALESCE(SUM(actual_amount), 0) FROM bus_payment WHERE deleted = 0 AND status = 2")
    java.math.BigDecimal sumReceivedAmount();

    /** 工单总数 */
    @Select("SELECT COUNT(*) FROM ser_order WHERE deleted = 0")
    Long countOrders();

    /** 待处理工单数（status != 3 已完成） */
    @Select("SELECT COUNT(*) FROM ser_order WHERE deleted = 0 AND status != 3")
    Long countPendingOrders();

    /** 已完成工单数 */
    @Select("SELECT COUNT(*) FROM ser_order WHERE deleted = 0 AND status = 3")
    Long countCompletedOrders();

    /** 平均满意度 */
    @Select("SELECT COALESCE(ROUND(AVG(satisfaction), 1), 0) FROM ser_order WHERE deleted = 0 AND satisfaction IS NOT NULL")
    java.math.BigDecimal avgSatisfaction();

    // ==================== 趋势数据 ====================

    /** 按月份统计趋势数据 */
    @Select("SELECT DATE_FORMAT(t.create_time, '%Y-%m') AS month, " +
            "SUM(t.new_customers) AS newCustomers, " +
            "SUM(t.new_opportunities) AS newOpportunities, " +
            "SUM(t.new_contracts) AS newContracts, " +
            "SUM(t.contract_amount) AS contractAmount " +
            "FROM ( " +
            "  SELECT create_time, 1 AS new_customers, 0 AS new_opportunities, 0 AS new_contracts, 0 AS contract_amount " +
            "  FROM cus_customer WHERE deleted = 0 " +
            "  UNION ALL " +
            "  SELECT create_time, 0, 1, 0, 0 " +
            "  FROM bus_opportunity WHERE deleted = 0 " +
            "  UNION ALL " +
            "  SELECT create_time, 0, 0, 1, COALESCE(amount, 0) " +
            "  FROM bus_contract WHERE deleted = 0 " +
            ") t " +
            "WHERE DATE_FORMAT(t.create_time, '%Y-%m') >= DATE_FORMAT(DATE_SUB(NOW(), INTERVAL #{months} MONTH), '%Y-%m') " +
            "GROUP BY month ORDER BY month")
    List<TrendVO> getTrend(@Param("months") int months);

    // ==================== 工单状态分布 ====================

    @Select("SELECT status, COUNT(*) AS count FROM ser_order WHERE deleted = 0 GROUP BY status ORDER BY status")
    List<ServiceStatsVO> getOrderStatusStats();

    // ==================== 销售业绩排行 ====================

    @Select("SELECT u.id AS userId, u.real_name AS realName, " +
            "COUNT(DISTINCT c.id) AS customerCount, " +
            "COUNT(DISTINCT o.id) AS opportunityCount, " +
            "SUM(CASE WHEN o.stage = 5 THEN 1 ELSE 0 END) AS wonCount, " +
            "COALESCE(SUM(DISTINCT ct.amount), 0) AS contractAmount, " +
            "COALESCE(SUM(DISTINCT p.actual_amount), 0) AS receivedAmount " +
            "FROM sys_user u " +
            "LEFT JOIN cus_customer c ON c.owner_id = u.id AND c.deleted = 0 " +
            "LEFT JOIN bus_opportunity o ON o.owner_id = u.id AND o.deleted = 0 " +
            "LEFT JOIN bus_contract ct ON ct.owner_id = u.id AND ct.deleted = 0 " +
            "LEFT JOIN bus_payment p ON p.owner_id = u.id AND p.deleted = 0 AND p.status = 2 " +
            "WHERE u.deleted = 0 AND u.status = 1 " +
            "GROUP BY u.id, u.real_name " +
            "ORDER BY contractAmount DESC")
    List<SalesRankingVO> getSalesRanking();

    // ==================== 客户行业分布 ====================

    @Select("SELECT COALESCE(industry, '未知') AS name, COUNT(*) AS count " +
            "FROM cus_customer WHERE deleted = 0 " +
            "GROUP BY industry ORDER BY count DESC")
    List<CustomerSourceVO> getCustomerIndustryStats();

    // ==================== 线索来源分布 ====================

    @Select("SELECT COALESCE(source, '未知') AS name, COUNT(*) AS count " +
            "FROM market_clue WHERE deleted = 0 " +
            "GROUP BY source ORDER BY count DESC")
    List<CustomerSourceVO> getClueSourceStats();

    // ==================== 自定义报表 ====================

    /** 客户统计（按条件筛选） */
    @Select("<script>" +
            "SELECT c.name AS customerName, c.industry, c.region, " +
            "CASE c.customer_level WHEN 1 THEN '普通' WHEN 2 THEN '重要' WHEN 3 THEN 'VIP' END AS customerLevel, " +
            "COALESCE(o.opp_count, 0) AS opportunityCount, " +
            "COALESCE(ct.contract_amount, 0) AS totalAmount " +
            "FROM cus_customer c " +
            "LEFT JOIN (SELECT customer_id, COUNT(*) AS opp_count FROM bus_opportunity WHERE deleted = 0 GROUP BY customer_id) o ON o.customer_id = c.id " +
            "LEFT JOIN (SELECT customer_id, SUM(amount) AS contract_amount FROM bus_contract WHERE deleted = 0 GROUP BY customer_id) ct ON ct.customer_id = c.id " +
            "WHERE c.deleted = 0 " +
            "<if test='dto.ownerId != null'>AND c.owner_id = #{dto.ownerId}</if> " +
            "<if test='dto.startDate != null and dto.startDate != \"\"'>AND c.create_time >= #{dto.startDate}</if> " +
            "<if test='dto.endDate != null and dto.endDate != \"\"'>AND c.create_time &lt;= #{dto.endDate}</if> " +
            "ORDER BY c.create_time DESC</script>")
    List<java.util.Map<String, Object>> getCustomCustomerReport(@Param("dto") com.crm.report.dto.ReportQueryDTO dto);

    /** 销售统计（按条件筛选） */
    @Select("<script>" +
            "SELECT u.real_name AS realName, " +
            "COUNT(DISTINCT o.id) AS opportunityCount, " +
            "SUM(CASE WHEN o.stage = 5 THEN 1 ELSE 0 END) AS wonCount, " +
            "COUNT(DISTINCT ct.id) AS contractCount, " +
            "COALESCE(SUM(DISTINCT ct.amount), 0) AS contractAmount, " +
            "COALESCE(SUM(DISTINCT p.actual_amount), 0) AS receivedAmount, " +
            "CASE WHEN COUNT(DISTINCT o.id) > 0 THEN ROUND(SUM(CASE WHEN o.stage = 5 THEN 1 ELSE 0 END) * 100.0 / COUNT(DISTINCT o.id), 1) ELSE 0 END AS winRate " +
            "FROM sys_user u " +
            "LEFT JOIN bus_opportunity o ON o.owner_id = u.id AND o.deleted = 0 " +
            "<if test='dto.startDate != null and dto.startDate != \"\"'>AND o.create_time >= #{dto.startDate}</if> " +
            "<if test='dto.endDate != null and dto.endDate != \"\"'>AND o.create_time &lt;= #{dto.endDate}</if> " +
            "LEFT JOIN bus_contract ct ON ct.owner_id = u.id AND ct.deleted = 0 " +
            "<if test='dto.startDate != null and dto.startDate != \"\"'>AND ct.create_time >= #{dto.startDate}</if> " +
            "<if test='dto.endDate != null and dto.endDate != \"\"'>AND ct.create_time &lt;= #{dto.endDate}</if> " +
            "LEFT JOIN bus_payment p ON p.owner_id = u.id AND p.deleted = 0 AND p.status = 2 " +
            "<if test='dto.startDate != null and dto.startDate != \"\"'>AND p.create_time >= #{dto.startDate}</if> " +
            "<if test='dto.endDate != null and dto.endDate != \"\"'>AND p.create_time &lt;= #{dto.endDate}</if> " +
            "WHERE u.deleted = 0 AND u.status = 1 " +
            "<if test='dto.ownerId != null'>AND u.id = #{dto.ownerId}</if> " +
            "GROUP BY u.id, u.real_name ORDER BY contractAmount DESC</script>")
    List<java.util.Map<String, Object>> getCustomSalesReport(@Param("dto") com.crm.report.dto.ReportQueryDTO dto);

    // ==================== 预测分析 ====================

    /**
     * 查询高流失风险客户（非公海、未删除、最后跟进时间早于阈值）
     * @param thresholdDays 阈值天数（超过此天数未跟进视为有流失风险）
     */
    @Select("SELECT c.id AS customerId, c.name AS customerName, c.customer_level AS customerLevel, " +
            "c.owner_id AS ownerId, u.real_name AS ownerName, " +
            "c.last_follow_time AS lastFollowTime, " +
            "c.total_amount AS totalAmount, " +
            "DATEDIFF(NOW(), IFNULL(c.last_follow_time, c.create_time)) AS daysSinceLastFollow " +
            "FROM cus_customer c " +
            "LEFT JOIN sys_user u ON u.id = c.owner_id AND u.deleted = 0 " +
            "WHERE c.deleted = 0 AND c.in_pool = 0 " +
            "AND (c.last_follow_time IS NULL OR c.last_follow_time < DATE_SUB(NOW(), INTERVAL #{thresholdDays} DAY)) " +
            "ORDER BY daysSinceLastFollow ASC")
    List<com.crm.report.vo.ChurnRiskVO> getChurnRiskCustomers(@Param("thresholdDays") int thresholdDays);

    /**
     * 查询客户负责人ID
     */
    @Select("SELECT owner_id FROM cus_customer WHERE id = #{customerId} AND deleted = 0")
    Long getCustomerOwnerId(@Param("customerId") Long customerId);

    /**
     * 插入挽留提醒消息到 sys_message 表
     */
    @org.apache.ibatis.annotations.Insert(
            "INSERT INTO sys_message(user_id, title, content, type, ref_id, ref_type, is_read, create_time) " +
            "VALUES(#{userId}, #{title}, #{content}, 3, #{refId}, #{refType}, 0, NOW())")
    int insertMessage(@Param("userId") Long userId,
                      @Param("title") String title,
                      @Param("content") String content,
                      @Param("refId") Long refId,
                      @Param("refType") String refType);
}
