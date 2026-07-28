package com.crm.system.vo;

import lombok.Data;

import java.util.List;

/**
 * 菜单树VO
 *
 * @author CRM
 */
@Data
public class MenuTreeVO {

    /** 菜单ID */
    private Long id;

    /** 父菜单ID */
    private Long parentId;

    /** 菜单名称 */
    private String name;

    /** 路由地址 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 图标 */
    private String icon;

    /** 菜单类型（1目录 2菜单 3按钮） */
    private Integer type;

    /** 权限标识 */
    private String perms;

    /** 排序 */
    private Integer orderNum;

    /** 是否可见（0隐藏 1可见） */
    private Integer visible;

    /** 子菜单 */
    private List<MenuTreeVO> children;
}
