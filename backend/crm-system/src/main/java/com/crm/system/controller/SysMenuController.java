package com.crm.system.controller;

import com.crm.common.api.R;
import com.crm.system.annotation.Log;
import com.crm.system.entity.SysMenu;
import com.crm.system.service.ISysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
    @PostMapping
    public R add(@RequestBody SysMenu menu) {
        return menuService.save(menu) ? R.ok() : R.fail("新增失败");
    }

    /**
     * 修改菜单
     */
    @Operation(summary = "修改菜单")
    @Log("修改菜单")
    @PutMapping
    public R update(@RequestBody SysMenu menu) {
        return menuService.updateById(menu) ? R.ok() : R.fail("修改失败");
    }

    /**
     * 删除菜单
     */
    @Operation(summary = "删除菜单")
    @Log("删除菜单")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return menuService.removeById(id) ? R.ok() : R.fail("删除失败");
    }
}
