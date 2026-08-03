package com.crm.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crm.common.api.R;
import com.crm.common.constant.Perms;
import com.crm.system.annotation.Log;
import com.crm.system.entity.SysMenu;
import com.crm.system.service.ISysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜单控制器
 *
 * @author CRM
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    @Autowired
    private ISysMenuService menuService;

    /**
     * 查询菜单树
     */
    @Operation(summary = "查询菜单树")
    @GetMapping("/tree")
    public R tree() {
        return R.ok(menuService.getAllMenuTree());
    }

    /**
     * 查询菜单详情
     */
    @Operation(summary = "查询菜单详情")
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        return R.ok(menuService.getById(id));
    }

    /**
     * 新增菜单
     */
    @Operation(summary = "新增菜单")
    @Log("新增菜单")
    @PreAuthorize("hasAuthority('" + Perms.MENU_ADD + "')")
    @PostMapping
    public R add(@RequestBody SysMenu menu) {
        return menuService.save(menu) ? R.ok() : R.fail("新增失败");
    }

    /**
     * 修改菜单
     */
    @Operation(summary = "修改菜单")
    @Log("修改菜单")
    @PreAuthorize("hasAuthority('" + Perms.MENU_EDIT + "')")
    @PutMapping
    public R update(@RequestBody SysMenu menu) {
        // 重名校验（同父级+同类型+同名，排除自身）
        Long count = menuService.count(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, menu.getParentId())
                .eq(SysMenu::getName, menu.getName())
                .eq(SysMenu::getType, menu.getType())
                .ne(SysMenu::getId, menu.getId()));
        if (count > 0) {
            return R.fail("同一父菜单下已存在同类型同名菜单");
        }
        return menuService.updateById(menu) ? R.ok() : R.fail("修改失败");
    }

    /**
     * 删除菜单
     */
    @Operation(summary = "删除菜单")
    @Log("删除菜单")
    @PreAuthorize("hasAuthority('" + Perms.MENU_DELETE + "')")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return menuService.removeById(id) ? R.ok() : R.fail("删除失败");
    }
}
