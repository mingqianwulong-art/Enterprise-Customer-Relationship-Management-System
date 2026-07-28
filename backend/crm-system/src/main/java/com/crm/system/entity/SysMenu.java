package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单/权限实体类
 *
 * @author CRM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    /** 菜单ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父菜单ID */
    @TableField("parent_id")
    private Long parentId;

    /** 菜单名称 */
    @TableField("name")
    private String name;

    /** 路由地址 */
    @TableField("path")
    private String path;

    /** 组件路径 */
    @TableField("component")
    private String component;

    /** 权限标识 */
    @TableField("perms")
    private String perms;

    /** 图标 */
    @TableField("icon")
    private String icon;

    /** 菜单类型（1目录 2菜单 3按钮） */
    @TableField("type")
    private Integer type;

    /** 排序 */
    @TableField("order_num")
    private Integer orderNum;

    /** 是否可见（0隐藏 1可见） */
    @TableField("visible")
    private Integer visible;

    /** 状态（0停用 1启用） */
    @TableField("status")
    private Integer status;
}
