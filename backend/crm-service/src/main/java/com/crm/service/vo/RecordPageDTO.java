package com.crm.service.vo;

import lombok.Data;

/**
 * 售后记录分页查询条件
 *
 * @author CRM
 */
@Data
public class RecordPageDTO {

    /** 当前页 */
    private Integer pageNum = 1;

    /** 每页条数 */
    private Integer pageSize = 10;

    /** 客户ID */
    private Long customerId;

    /** 记录类型 */
    private Integer type;

    /** 关联工单ID */
    private Long orderId;
}
