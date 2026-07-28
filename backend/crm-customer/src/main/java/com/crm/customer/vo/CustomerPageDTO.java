package com.crm.customer.vo;

import lombok.Data;

/**
 * 客户分页查询条件
 *
 * @author CRM
 */
@Data
public class CustomerPageDTO {

    /** 当前页 */
    private Integer pageNum = 1;

    /** 每页条数 */
    private Integer pageSize = 10;

    /** 客户名称（模糊查询） */
    private String name;

    /** 所属行业 */
    private String industry;

    /** 所在区域 */
    private String region;

    /** 负责人ID */
    private Long ownerId;

    /** 客户等级（1普通 2重要 3VIP） */
    private Integer level;
}
