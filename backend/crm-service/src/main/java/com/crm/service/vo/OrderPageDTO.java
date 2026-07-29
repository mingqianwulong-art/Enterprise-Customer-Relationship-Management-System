package com.crm.service.vo;

import lombok.Data;

/**
 * 工单分页查询条件
 *
 * @author CRM
 */
@Data
public class OrderPageDTO {

    /** 当前页 */
    private Integer pageNum = 1;

    /** 每页条数 */
    private Integer pageSize = 10;

    /** 工单编号（模糊查询） */
    private String orderNo;

    /** 客户ID */
    private Long customerId;

    /** 工单类型 */
    private Integer type;

    /** 状态 */
    private Integer status;

    /** 优先级 */
    private Integer priority;
}
