package com.crm.report.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 趋势数据 VO
 */
@Data
public class TrendVO {

    /** 月份（yyyy-MM） */
    private String month;

    /** 新增客户数 */
    private Long newCustomers;

    /** 新增商机数 */
    private Long newOpportunities;

    /** 新增合同数 */
    private Long newContracts;

    /** 合同金额 */
    private BigDecimal contractAmount;
}
