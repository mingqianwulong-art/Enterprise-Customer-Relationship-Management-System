package com.crm.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.system.entity.SysMenu;
import com.crm.system.vo.MenuTreeVO;

import java.util.List;
import java.util.Set;

/**
 * 菜单服务接口
 *
 * @author CRM
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * 查询用户菜单树
     *
     * @param userId 用户ID
     * @return 菜单树
     */
    List<MenuTreeVO> getMenuTreeByUserId(Long userId);

    /**
     * 查询用户权限标识集合
     *
     * @param userId 用户ID
     * @return 权限标识集合
     */
    Set<String> getPermissionsByUserId(Long userId);

    /**
     * 查询所有菜单树（用于菜单管理）
     *
     * @return 菜单树
     */
    List<MenuTreeVO> getAllMenuTree();

    /**
     * 查询所有已停用（status=0）的菜单路径集合（type=2 菜单）
     * 用于前端拦截点击并提示"该功能已被停用"
     *
     * @return 停用菜单路径集合
     */
    Set<String> getDisabledPaths();
}
