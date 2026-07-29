package com.crm.report.vo;

import lombok.Data;

/**
 * 售后服务统计 VO
 */
@Data
public class ServiceStatsVO {

    /** 工单状态名称 */
    private String statusName;

    /** 工单状态值 */
    private Integer status;

    /** 数量 */
    private Long count;
}
