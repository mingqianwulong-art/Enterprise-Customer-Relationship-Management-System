package com.crm.system.controller;

import com.crm.common.api.R;
import com.crm.common.constant.Perms;
import com.crm.system.annotation.Log;
import com.crm.system.dto.UserPageDTO;
import com.crm.system.entity.SysUser;
import com.crm.system.service.ISysUserService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 *
 * @author CRM
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    /**
     * 分页查询用户
     */
    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public R page(UserPageDTO dto) {
        return R.ok(userService.page(dto));
    }

    /**
     * 查询用户详情
     */
    @Operation(summary = "查询用户详情")
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return R.ok(user);
    }

    /**
     * 新增用户
     */
    @Operation(summary = "新增用户")
    @Log("新增用户")
    @PreAuthorize("hasAuthority('" + Perms.USER_ADD + "')")
    @PostMapping
    public R add(@RequestBody SysUser user) {
        return userService.addUser(user) ? R.ok() : R.fail("新增失败");
    }

    /**
     * 修改用户
     */
    @Operation(summary = "修改用户")
    @Log("修改用户")
    @PreAuthorize("hasAuthority('" + Perms.USER_EDIT + "')")
    @PutMapping
    public R update(@RequestBody SysUser user) {
        return userService.updateUser(user) ? R.ok() : R.fail("修改失败");
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户")
    @Log("删除用户")
    @PreAuthorize("hasAuthority('" + Perms.USER_DELETE + "')")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return userService.deleteUser(id) ? R.ok() : R.fail("删除失败");
    }

    /**
     * 重置密码
     */
    @Operation(summary = "重置密码")
    @Log("重置密码")
    @PreAuthorize("hasAuthority('" + Perms.USER_RESET + "')")
    @PutMapping("/resetPwd")
    public R resetPwd(@RequestParam Long userId, @RequestParam String password) {
        return userService.resetPassword(userId, password) ? R.ok() : R.fail("重置失败");
    }

    /**
     * 修改状态
     */
    @Operation(summary = "修改用户状态")
    @Log("修改用户状态")
    @PreAuthorize("hasAuthority('" + Perms.USER_EDIT + "')")
    @PutMapping("/changeStatus")
    public R changeStatus(@RequestParam Long userId, @RequestParam Integer status) {
        return userService.changeStatus(userId, status) ? R.ok() : R.fail("修改失败");
    }

    /**
     * 查询用户已分配的角色ID列表
     */
    @Operation(summary = "查询用户角色")
    @PreAuthorize("hasAuthority('" + Perms.USER_ASSIGN + "')")
    @GetMapping("/{userId}/roles")
    public R getUserRoles(@PathVariable Long userId) {
        return R.ok(userService.getUserRoleIds(userId));
    }

    /**
     * 分配角色
     */
    @Operation(summary = "分配角色")
    @Log("分配角色")
    @PreAuthorize("hasAuthority('" + Perms.USER_ASSIGN + "')")
    @PutMapping("/{userId}/roles")
    public R assignRoles(@PathVariable Long userId, @RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Long> roleIds = (List<Long>) body.get("roleIds");
        return userService.assignRoles(userId, roleIds) ? R.ok() : R.fail("分配失败");
    }
}
