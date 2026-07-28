package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * @author CRM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /** 用户ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 部门ID */
    @TableField("dept_id")
    private Long deptId;

    /** 用户名 */
    @TableField("username")
    private String username;

    /** 密码 */
    @TableField("password")
    private String password;

    /** 真实姓名 */
    @TableField("real_name")
    private String realName;

    /** 手机号 */
    @TableField("phone")
    private String phone;

    /** 邮箱 */
    @TableField("email")
    private String email;

    /** 性别（0未知 1男 2女） */
    @TableField("sex")
    private Integer sex;

    /** 头像地址 */
    @TableField("avatar")
    private String avatar;

    /** 状态（0停用 1启用） */
    @TableField("status")
    private Integer status;

    /** 最后登录时间 */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
}
