package com.crm.system.vo;

import lombok.Data;

/**
 * 登录返回VO
 *
 * @author CRM
 */
@Data
public class LoginVO {

    /** 访问令牌 */
    private String token;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 真实姓名 */
    private String realName;
}
