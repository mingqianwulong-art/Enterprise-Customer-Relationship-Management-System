package com.crm.report.vo;

import lombok.Data;

/**
 * 客户来源分布 VO
 */
@Data
public class CustomerSourceVO {

    /** 来源/行业名称 */
    private String name;

    /** 数量 */
    private Long count;
}
