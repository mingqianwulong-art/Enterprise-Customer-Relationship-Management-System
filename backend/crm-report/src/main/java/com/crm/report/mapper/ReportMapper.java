package com.crm.report.mapper;

import com.crm.report.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 报表 Mapper —— 跨模块聚合查询
 * <p>
 * 所有业务统计均支持 ownerIds 数据权限过滤：
 * - ownerIds=null：不过滤（数据范围全部）
 * - ownerIds=非空：仅统计指定负责人/受理人范围的数据
 */
@Mapper
public interface ReportMapper {

    // ==================== 概览统计 ====================

    @Select("<script>" +
            "SELECT COUNT(*) FROM cus_customer WHERE deleted = 0 " +
            "<if test='ownerIds != null'>AND owner_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "</script>")
    Long countCustomers(@Param("ownerIds") List<Long> ownerIds);

    @Select("<script>" +
            "SELECT COUNT(*) FROM cus_customer WHERE deleted = 0 " +
            "AND DATE_FORMAT(create_time, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m') " +
            "<if test='ownerIds != null'>AND owner_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "</script>")
    Long countMonthlyNewCustomers(@Param("ownerIds") List<Long> ownerIds);

    @Select("<script>" +
            "SELECT COUNT(*) FROM cus_customer WHERE deleted = 0 AND in_pool = 1 " +
            "<if test='ownerIds != null'>AND owner_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "</script>")
    Long countPoolCustomers(@Param("ownerIds") List<Long> ownerIds);

    @Select("<script>" +
            "SELECT COUNT(*) FROM market_clue WHERE deleted = 0 " +
            "<if test='ownerIds != null'>AND owner_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "</script>")
    Long countClues(@Param("ownerIds") List<Long> ownerIds);

    @Select("<script>" +
            "SELECT COUNT(*) FROM market_clue WHERE deleted = 0 AND status = 2 " +
            "<if test='ownerIds != null'>AND owner_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "</script>")
    Long countConvertedClues(@Param("ownerIds") List<Long> ownerIds);

    @Select("<script>" +
            "SELECT COUNT(*) FROM bus_opportunity WHERE deleted = 0 " +
            "<if test='ownerIds != null'>AND owner_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "</script>")
    Long countOpportunities(@Param("ownerIds") List<Long> ownerIds);

    @Select("<script>" +
            "SELECT COUNT(*) FROM bus_opportunity WHERE deleted = 0 AND stage BETWEEN 1 AND 4 " +
            "<if test='ownerIds != null'>AND owner_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "</script>")
    Long countActiveOpportunities(@Param("ownerIds") List<Long> ownerIds);

    @Select("<script>" +
            "SELECT COUNT(*) FROM bus_opportunity WHERE deleted = 0 AND stage = 5 " +
            "<if test='ownerIds != null'>AND owner_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "</script>")
    Long countWonOpportunities(@Param("ownerIds") List<Long> ownerIds);

    @Select("<script>" +
            "SELECT COUNT(*) FROM bus_contract WHERE deleted = 0 " +
            "<if test='ownerIds != null'>AND owner_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "</script>")
    Long countContracts(@Param("ownerIds") List<Long> ownerIds);

    @Select("<script>" +
            "SELECT COALESCE(SUM(amount), 0) FROM bus_contract WHERE deleted = 0 " +
            "<if test='ownerIds != null'>AND owner_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "</script>")
    java.math.BigDecimal sumContractAmount(@Param("ownerIds") List<Long> ownerIds);

    @Select("<script>" +
            "SELECT COALESCE(SUM(actual_amount), 0) FROM bus_payment WHERE deleted = 0 AND status = 2 " +
            "<if test='ownerIds != null'>AND owner_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "</script>")
    java.math.BigDecimal sumReceivedAmount(@Param("ownerIds") List<Long> ownerIds);

    @Select("<script>" +
            "SELECT COUNT(*) FROM ser_order WHERE deleted = 0 " +
            "<if test='ownerIds != null'>AND assignee_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "</script>")
    Long countOrders(@Param("ownerIds") List<Long> ownerIds);

    @Select("<script>" +
            "SELECT COUNT(*) FROM ser_order WHERE deleted = 0 AND status != 3 " +
            "<if test='ownerIds != null'>AND assignee_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "</script>")
    Long countPendingOrders(@Param("ownerIds") List<Long> ownerIds);

    @Select("<script>" +
            "SELECT COUNT(*) FROM ser_order WHERE deleted = 0 AND status = 3 " +
            "<if test='ownerIds != null'>AND assignee_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "</script>")
    Long countCompletedOrders(@Param("ownerIds") List<Long> ownerIds);

    @Select("<script>" +
            "SELECT COALESCE(ROUND(AVG(satisfaction), 1), 0) FROM ser_order " +
            "WHERE deleted = 0 AND satisfaction IS NOT NULL " +
            "<if test='ownerIds != null'>AND assignee_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "</script>")
    java.math.BigDecimal avgSatisfaction(@Param("ownerIds") List<Long> ownerIds);

    // ==================== 趋势数据 ====================

    @Select("<script>" +
            "SELECT DATE_FORMAT(t.create_time, '%Y年%m月') AS month, " +
            "SUM(t.new_customers) AS newCustomers, " +
            "SUM(t.new_opportunities) AS newOpportunities, " +
            "SUM(t.new_contracts) AS newContracts, " +
            "SUM(t.contract_amount) AS contractAmount " +
            "FROM ( " +
            "  SELECT create_time, 1 AS new_customers, 0 AS new_opportunities, 0 AS new_contracts, 0 AS contract_amount " +
            "  FROM cus_customer WHERE deleted = 0 " +
            "  <if test='ownerIds != null'>AND owner_id IN " +
            "  <foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "  UNION ALL " +
            "  SELECT create_time, 0, 1, 0, 0 " +
            "  FROM bus_opportunity WHERE deleted = 0 " +
            "  <if test='ownerIds != null'>AND owner_id IN " +
            "  <foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "  UNION ALL " +
            "  SELECT create_time, 0, 0, 1, COALESCE(amount, 0) " +
            "  FROM bus_contract WHERE deleted = 0 " +
            "  <if test='ownerIds != null'>AND owner_id IN " +
            "  <foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            ") t " +
            "WHERE DATE_FORMAT(t.create_time, '%Y-%m') >= DATE_FORMAT(DATE_SUB(NOW(), INTERVAL #{months} MONTH), '%Y-%m') " +
            "GROUP BY month ORDER BY month" +
            "</script>")
    List<TrendVO> getTrend(@Param("months") int months, @Param("ownerIds") List<Long> ownerIds);

    // ==================== 工单状态分布 ====================

    @Select("<script>" +
            "SELECT status, COUNT(*) AS count FROM ser_order WHERE deleted = 0 " +
            "<if test='ownerIds != null'>AND assignee_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            " GROUP BY status ORDER BY status" +
            "</script>")
    List<ServiceStatsVO> getOrderStatusStats(@Param("ownerIds") List<Long> ownerIds);

    // ==================== 销售业绩排行 ====================

    @Select("<script>" +
            "SELECT u.id AS userId, u.real_name AS realName, " +
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
            "<if test='ownerIds != null'>AND u.id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            " GROUP BY u.id, u.real_name " +
            "ORDER BY contractAmount DESC" +
            "</script>")
    List<SalesRankingVO> getSalesRanking(@Param("ownerIds") List<Long> ownerIds);

    // ==================== 客户行业分布 ====================

    @Select("<script>" +
            "SELECT COALESCE(industry, '未知') AS name, COUNT(*) AS count " +
            "FROM cus_customer WHERE deleted = 0 " +
            "<if test='ownerIds != null'>AND owner_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            " GROUP BY industry ORDER BY count DESC" +
            "</script>")
    List<CustomerSourceVO> getCustomerIndustryStats(@Param("ownerIds") List<Long> ownerIds);

    // ==================== 线索来源分布 ====================

    @Select("<script>" +
            "SELECT COALESCE(source, '未知') AS name, COUNT(*) AS count " +
            "FROM market_clue WHERE deleted = 0 " +
            "<if test='ownerIds != null'>AND owner_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            " GROUP BY source ORDER BY count DESC" +
            "</script>")
    List<CustomerSourceVO> getClueSourceStats(@Param("ownerIds") List<Long> ownerIds);

    // ==================== 自定义报表 ====================

    @Select("<script>" +
            "SELECT c.name AS customerName, c.industry, c.region, " +
            "CASE c.customer_level WHEN 1 THEN '普通' WHEN 2 THEN '重要' WHEN 3 THEN 'VIP' END AS customerLevel, " +
            "COALESCE(o.opp_count, 0) AS opportunityCount, " +
            "COALESCE(ct.contract_amount, 0) AS totalAmount " +
            "FROM cus_customer c " +
            "LEFT JOIN (SELECT customer_id, COUNT(*) AS opp_count FROM bus_opportunity WHERE deleted = 0 GROUP BY customer_id) o ON o.customer_id = c.id " +
            "LEFT JOIN (SELECT customer_id, SUM(amount) AS contract_amount FROM bus_contract WHERE deleted = 0 GROUP BY customer_id) ct ON ct.customer_id = c.id " +
            "WHERE c.deleted = 0 " +
            "<if test='ownerIds != null'>AND c.owner_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "<if test='dto.ownerId != null'>AND c.owner_id = #{dto.ownerId}</if> " +
            "<if test='dto.startDate != null and dto.startDate != \"\"'>AND c.create_time >= #{dto.startDate}</if> " +
            "<if test='dto.endDate != null and dto.endDate != \"\"'>AND c.create_time &lt;= #{dto.endDate}</if> " +
            "ORDER BY c.create_time DESC</script>")
    List<java.util.Map<String, Object>> getCustomCustomerReport(
            @Param("dto") com.crm.report.dto.ReportQueryDTO dto,
            @Param("ownerIds") List<Long> ownerIds);

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
            "<if test='ownerIds != null'>AND u.id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            "<if test='dto.ownerId != null'>AND u.id = #{dto.ownerId}</if> " +
            "GROUP BY u.id, u.real_name ORDER BY contractAmount DESC</script>")
    List<java.util.Map<String, Object>> getCustomSalesReport(
            @Param("dto") com.crm.report.dto.ReportQueryDTO dto,
            @Param("ownerIds") List<Long> ownerIds);

    // ==================== 预测分析 ====================

    @Select("<script>" +
            "SELECT c.id AS customerId, c.name AS customerName, c.customer_level AS customerLevel, " +
            "c.owner_id AS ownerId, u.real_name AS ownerName, " +
            "c.last_follow_time AS lastFollowTime, " +
            "c.total_amount AS totalAmount, " +
            "DATEDIFF(NOW(), IFNULL(c.last_follow_time, c.create_time)) AS daysSinceLastFollow " +
            "FROM cus_customer c " +
            "LEFT JOIN sys_user u ON u.id = c.owner_id AND u.deleted = 0 " +
            "WHERE c.deleted = 0 AND c.in_pool = 0 " +
            "AND (c.last_follow_time IS NULL OR c.last_follow_time &lt; DATE_SUB(NOW(), INTERVAL #{thresholdDays} DAY)) " +
            "<if test='ownerIds != null'>AND c.owner_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></if>" +
            " ORDER BY daysSinceLastFollow ASC" +
            "</script>")
    List<com.crm.report.vo.ChurnRiskVO> getChurnRiskCustomers(
            @Param("thresholdDays") int thresholdDays,
            @Param("ownerIds") List<Long> ownerIds);

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
