package com.crm.business.vo;

import lombok.Data;

/**
 * 合同分页查询条件
 *
 * @author CRM
 */
@Data
public class ContractPageDTO {

    /** 当前页 */
    private Integer pageNum = 1;

    /** 每页条数 */
    private Integer pageSize = 10;

    /** 合同名称（模糊查询） */
    private String contractName;

    /** 客户ID */
    private Long customerId;

    /** 状态 */
    private Integer status;
}
