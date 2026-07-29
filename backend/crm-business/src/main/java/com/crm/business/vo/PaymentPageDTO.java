package com.crm.business.vo;

import lombok.Data;

/**
 * 回款分页查询条件
 *
 * @author CRM
 */
@Data
public class PaymentPageDTO {

    /** 当前页 */
    private Integer pageNum = 1;

    /** 每页条数 */
    private Integer pageSize = 10;

    /** 合同ID */
    private Long contractId;

    /** 客户ID */
    private Long customerId;

    /** 状态 */
    private Integer status;
}
