package com.crm.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crm.common.api.R;
import com.crm.common.constant.Perms;
import com.crm.system.annotation.Log;
import com.crm.system.entity.SysDept;
import com.crm.system.service.ISysDeptService;
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
 * 部门控制器
 *
 * @author CRM
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/system/dept")
public class SysDeptController {

    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询部门树
     */
    @Operation(summary = "查询部门树")
    @GetMapping("/tree")
    public R tree() {
        return R.ok(deptService.listDeptTree());
    }

    /**
     * 查询部门详情
     */
    @Operation(summary = "查询部门详情")
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        return R.ok(deptService.getById(id));
    }

    /**
     * 新增部门
     */
    @Operation(summary = "新增部门")
    @Log("新增部门")
    @PreAuthorize("hasAuthority('" + Perms.DEPT_ADD + "')")
    @PostMapping
    public R add(@RequestBody SysDept dept) {
        return deptService.save(dept) ? R.ok() : R.fail("新增失败");
    }

    /**
     * 修改部门
     */
    @Operation(summary = "修改部门")
    @Log("修改部门")
    @PreAuthorize("hasAuthority('" + Perms.DEPT_EDIT + "')")
    @PutMapping
    public R update(@RequestBody SysDept dept) {
        // 重名校验（排除自身）
        Long count = deptService.count(new LambdaQueryWrapper<SysDept>()
                .eq(SysDept::getParentId, dept.getParentId())
                .eq(SysDept::getDeptName, dept.getDeptName())
                .ne(SysDept::getId, dept.getId()));
        if (count > 0) {
            return R.fail("同一父部门下已存在同名部门");
        }
        return deptService.updateById(dept) ? R.ok() : R.fail("修改失败");
    }

    /**
     * 删除部门
     */
    @Operation(summary = "删除部门")
    @Log("删除部门")
    @PreAuthorize("hasAuthority('" + Perms.DEPT_DELETE + "')")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return deptService.removeById(id) ? R.ok() : R.fail("删除失败");
    }
}
