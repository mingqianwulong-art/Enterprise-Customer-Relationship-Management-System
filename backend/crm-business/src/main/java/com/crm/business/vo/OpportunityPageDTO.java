package com.crm.business.vo;

import lombok.Data;

/**
 * 商机分页查询条件
 *
 * @author CRM
 */
@Data
public class OpportunityPageDTO {

    /** 当前页 */
    private Integer pageNum = 1;

    /** 每页条数 */
    private Integer pageSize = 10;

    /** 商机名称（模糊查询） */
    private String oppName;

    /** 客户ID */
    private Long customerId;

    /** 商机阶段 */
    private Integer stage;

    /** 负责人ID */
    private Long ownerId;
}
