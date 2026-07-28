package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户角色关联实体类
 * <p>
 * 无 BaseEntity 字段
 *
 * @author CRM
 */
@Data
@TableName("sys_user_role")
public class SysUserRole {

    /** 用户ID */
    @TableField("user_id")
    private Long userId;

    /** 角色ID */
    @TableField("role_id")
    private Long roleId;
}
