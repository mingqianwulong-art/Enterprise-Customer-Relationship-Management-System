package com.crm.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.api.R;
import com.crm.system.annotation.Log;
import com.crm.system.entity.SysRole;
import com.crm.system.service.ISysRoleService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色控制器
 *
 * @author CRM
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Autowired
    private ISysRoleService roleService;

    /**
     * 分页查询角色
     */
    @Operation(summary = "分页查询角色")
    @GetMapping("/page")
    public R page(@RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize,
                  @RequestParam(required = false) String roleName) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .like(roleName != null && !roleName.isEmpty(), SysRole::getRoleName, roleName)
                .orderByDesc(SysRole::getCreateTime);
        return R.ok(roleService.page(page, wrapper));
    }

    /**
     * 查询角色详情
     */
    @Operation(summary = "查询角色详情")
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        return R.ok(roleService.getById(id));
    }

    /**
     * 新增角色
     */
    @Operation(summary = "新增角色")
    @Log("新增角色")
    @PostMapping
    public R add(@RequestBody SysRole role) {
        return roleService.save(role) ? R.ok() : R.fail("新增失败");
    }

    /**
     * 修改角色
     */
    @Operation(summary = "修改角色")
    @Log("修改角色")
    @PutMapping
    public R update(@RequestBody SysRole role) {
        return roleService.updateById(role) ? R.ok() : R.fail("修改失败");
    }

    /**
     * 删除角色
     */
    @Operation(summary = "删除角色")
    @Log("删除角色")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return roleService.removeById(id) ? R.ok() : R.fail("删除失败");
    }

    /**
     * 查询所有角色（下拉选择用）
     */
    @Operation(summary = "查询所有角色（下拉选择）")
    @GetMapping("/list")
    public R list() {
        return R.ok(roleService.list(new LambdaQueryWrapper<SysRole>().eq(SysRole::getStatus, "0")));
    }
}
