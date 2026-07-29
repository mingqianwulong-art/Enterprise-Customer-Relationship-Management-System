package com.crm.report.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 数据看板概览统计 VO
 */
@Data
public class DashboardOverviewVO {

    /** 客户总数 */
    private Long totalCustomers;

    /** 本月新增客户 */
    private Long monthlyNewCustomers;

    /** 公海客户数 */
    private Long poolCustomers;

    /** 线索总数 */
    private Long totalClues;

    /** 已转化线索数 */
    private Long convertedClues;

    /** 线索转化率(%) */
    private BigDecimal clueConversionRate;

    /** 商机总数 */
    private Long totalOpportunities;

    /** 进行中商机数 */
    private Long activeOpportunities;

    /** 已赢单商机数 */
    private Long wonOpportunities;

    /** 赢单率(%) */
    private BigDecimal winRate;

    /** 合同总数 */
    private Long totalContracts;

    /** 合同总金额 */
    private BigDecimal totalContractAmount;

    /** 已回款金额 */
    private BigDecimal totalReceivedAmount;

    /** 回款率(%) */
    private BigDecimal paymentRate;

    /** 工单总数 */
    private Long totalOrders;

    /** 待处理工单数 */
    private Long pendingOrders;

    /** 已完成工单数 */
    private Long completedOrders;

    /** 客户满意度均分 */
    private BigDecimal avgSatisfaction;
}
