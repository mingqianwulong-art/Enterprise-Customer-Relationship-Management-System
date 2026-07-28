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
}
