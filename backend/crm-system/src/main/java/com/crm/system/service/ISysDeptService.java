package com.crm.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.system.entity.SysDept;

import java.util.List;

/**
 * 部门服务接口
 *
 * @author CRM
 */
public interface ISysDeptService extends IService<SysDept> {

    /**
     * 查询部门树（返回所有部门，由前端构建树或由 service 构建平铺列表）
     *
     * @return 部门列表
     */
    List<SysDept> listDeptTree();

    /**
     * 查询部门及其所有子部门ID列表（含自身）
     *
     * @param deptId 部门ID
     * @return 部门ID列表（含自身及所有子部门）
     */
    List<Long> getChildDeptIds(Long deptId);
}
