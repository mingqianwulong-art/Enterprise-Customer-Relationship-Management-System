package com.crm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.exception.BusinessException;
import com.crm.system.entity.SysMenu;
import com.crm.system.entity.SysRoleMenu;
import com.crm.system.entity.SysUserRole;
import com.crm.system.mapper.SysMenuMapper;
import com.crm.system.mapper.SysRoleMenuMapper;
import com.crm.system.mapper.SysUserRoleMapper;
import com.crm.system.service.ISysMenuService;
import com.crm.system.vo.MenuTreeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜单服务实现
 *
 * @author CRM
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Override
    public List<MenuTreeVO> getMenuTreeByUserId(Long userId) {
        // 获取用户角色ID
        List<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 获取菜单ID
        List<Long> menuIds = getRoleMenuIds(roleIds);
        if (menuIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 查询菜单（仅目录和菜单，排除按钮）
        List<SysMenu> menus = baseMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .in(SysMenu::getId, menuIds)
                .eq(SysMenu::getStatus, 1)
                .ne(SysMenu::getType, 3)
                .orderByAsc(SysMenu::getParentId)
                .orderByAsc(SysMenu::getOrderNum));
        return buildMenuTree(menus);
    }

    @Override
    public Set<String> getPermissionsByUserId(Long userId) {
        List<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return Collections.emptySet();
        }
        List<Long> menuIds = getRoleMenuIds(roleIds);
        if (menuIds.isEmpty()) {
            return Collections.emptySet();
        }
        List<SysMenu> menus = baseMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .in(SysMenu::getId, menuIds)
                .eq(SysMenu::getStatus, 1));
        return menus.stream()
                .map(SysMenu::getPerms)
                .filter(p -> p != null && !p.isEmpty())
                .collect(Collectors.toSet());
    }

    @Override
    public List<MenuTreeVO> getAllMenuTree() {
        // 菜单管理页面需展示全部菜单（含停用），不过滤 status
        List<SysMenu> menus = baseMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .ne(SysMenu::getType, 3)
                .orderByAsc(SysMenu::getParentId)
                .orderByAsc(SysMenu::getOrderNum));
        return buildMenuTree(menus);
    }

    /**
     * 查询所有已停用的菜单路径集合（含目录与菜单，支持目录级联停用）
     * 规则：
     *   1. status=0 的目录（type=1）路径加入集合
     *   2. status=0 的菜单（type=2）路径加入集合
     *   3. 父目录已停用的菜单（type=2）路径也加入集合（级联停用，防 URL 直接访问）
     * 前端左侧导航栏据此拦截点击并提示"该功能已被停用"
     */
    @Override
    public Set<String> getDisabledPaths() {
        // 1. 所有停用的目录（type=1, status=0）
        List<SysMenu> disabledDirs = baseMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getStatus, 0)
                .eq(SysMenu::getType, 1));
        Set<Long> disabledDirIds = disabledDirs.stream()
                .map(SysMenu::getId)
                .collect(Collectors.toSet());
        Set<String> disabledPaths = new java.util.HashSet<>();
        disabledDirs.stream()
                .map(SysMenu::getPath)
                .filter(p -> p != null && !p.isEmpty())
                .forEach(disabledPaths::add);

        // 2. 停用的菜单（type=2, status=0）+ 父目录已停用的菜单（级联）
        LambdaQueryWrapper<SysMenu> menuWrapper = new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getType, 2);
        if (!disabledDirIds.isEmpty()) {
            // status=0 OR parentId IN (停用目录ID)
            menuWrapper.and(w -> w.eq(SysMenu::getStatus, 0)
                    .or().in(SysMenu::getParentId, disabledDirIds));
        } else {
            menuWrapper.eq(SysMenu::getStatus, 0);
        }
        List<SysMenu> menus = baseMapper.selectList(menuWrapper);
        menus.stream()
                .map(SysMenu::getPath)
                .filter(p -> p != null && !p.isEmpty())
                .forEach(disabledPaths::add);

        return disabledPaths;
    }

    /**
     * 新增菜单（同级下同类型不允许重名）
     */
    @Override
    public boolean save(SysMenu entity) {
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, entity.getParentId())
                .eq(SysMenu::getName, entity.getName())
                .eq(SysMenu::getType, entity.getType()));
        if (count > 0) {
            throw new BusinessException("同一父菜单下已存在同类型同名菜单");
        }
        return super.save(entity);
    }

    /**
     * 获取用户角色ID列表
     */
    private List<Long> getUserRoleIds(Long userId) {
        List<SysUserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        return userRoles.stream().map(SysUserRole::getRoleId).distinct().collect(Collectors.toList());
    }

    /**
     * 获取角色对应的菜单ID列表
     */
    private List<Long> getRoleMenuIds(List<Long> roleIds) {
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIds));
        return roleMenus.stream().map(SysRoleMenu::getMenuId).distinct().collect(Collectors.toList());
    }

    /**
     * 构建菜单树
     */
    private List<MenuTreeVO> buildMenuTree(List<SysMenu> menus) {
        List<MenuTreeVO> voList = menus.stream().map(this::convertToVO).collect(Collectors.toList());
        return buildTree(voList, 0L);
    }

    /**
     * 递归构建树结构
     */
    private List<MenuTreeVO> buildTree(List<MenuTreeVO> all, Long parentId) {
        return all.stream()
                .filter(m -> Objects.equals(m.getParentId(), parentId))
                .peek(m -> m.setChildren(buildTree(all, m.getId())))
                .collect(Collectors.toList());
    }

    /**
     * 实体转VO
     */
    private MenuTreeVO convertToVO(SysMenu menu) {
        MenuTreeVO vo = new MenuTreeVO();
        vo.setId(menu.getId());
        vo.setParentId(menu.getParentId());
        vo.setName(menu.getName());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setIcon(menu.getIcon());
        vo.setType(menu.getType());
        vo.setPerms(menu.getPerms());
        vo.setOrderNum(menu.getOrderNum());
        vo.setVisible(menu.getVisible());
        vo.setStatus(menu.getStatus());
        return vo;
    }
}
