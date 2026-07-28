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
}
