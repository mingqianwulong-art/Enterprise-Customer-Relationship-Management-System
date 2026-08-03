package com.crm.system.vo;

import com.crm.system.entity.SysUser;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 登录用户信息VO
 *
 * @author CRM
 */
@Data
public class LoginUserVO {

    /** 用户基本信息 */
    private SysUser user;

    /** 角色编码列表 */
    private Set<String> roles;

    /** 权限标识列表 */
    private Set<String> permissions;

    /** 菜单树 */
    private List<MenuTreeVO> menus;

    /** 数据范围（1本人 2本部门 3本部门及下 4全部） */
    private Integer dataScope;

    /** 已停用的菜单路径集合（前端用于拦截点击并提示"该功能已被停用"） */
    private Set<String> disabledPaths;
}
