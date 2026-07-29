package com.crm.report.dto;

import lombok.Data;

/**
 * 报表查询 DTO
 */
@Data
public class ReportQueryDTO {

    /** 开始日期（yyyy-MM-dd） */
    private String startDate;

    /** 结束日期（yyyy-MM-dd） */
    private String endDate;

    /** 负责人ID */
    private Long ownerId;

    /** 部门ID */
    private Long deptId;
}
