package com.crm.system.dto;

import lombok.Data;

/**
 * 用户分页查询DTO
 *
 * @author CRM
 */
@Data
public class UserPageDTO {

    /** 当前页 */
    private Integer pageNum = 1;

    /** 每页条数 */
    private Integer pageSize = 10;

    /** 用户名（模糊） */
    private String username;

    /** 手机号（模糊） */
    private String phone;

    /** 状态（0停用 1启用） */
    private Integer status;
}
