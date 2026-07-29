package com.crm.business.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 销售漏斗统计VO
 *
 * @author CRM
 */
@Data
public class FunnelVO {

    /** 商机阶段 */
    private Integer stage;

    /** 阶段名称 */
    private String stageName;

    /** 商机数量 */
    private Integer count;

    /** 阶段总金额 */
    private BigDecimal totalAmount;

    /** 转化率 */
    private BigDecimal conversionRate;
}
