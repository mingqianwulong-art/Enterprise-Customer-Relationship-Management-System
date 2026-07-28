package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色菜单关联实体类
 * <p>
 * 无 BaseEntity 字段
 *
 * @author CRM
 */
@Data
@TableName("sys_role_menu")
public class SysRoleMenu {

    /** 角色ID */
    @TableField("role_id")
    private Long roleId;

    /** 菜单ID */
    @TableField("menu_id")
    private Long menuId;
}
