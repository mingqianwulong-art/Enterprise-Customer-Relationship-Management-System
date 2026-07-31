package com.crm.report.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 销售趋势预测 VO
 * <p>
 * 基于历史数据预测未来销售趋势
 *
 * @author CRM
 */
@Data
public class ForecastVO {

    /** 月份（yyyy-MM） */
    private String month;

    /** 预测合同金额 */
    private BigDecimal forecastAmount;

    /** 预测新增客户数 */
    private Long forecastCustomers;

    /** 预测新增商机数 */
    private Long forecastOpportunities;

    /** 历史实际数据（用于对比） */
    private List<TrendVO> history;
}
