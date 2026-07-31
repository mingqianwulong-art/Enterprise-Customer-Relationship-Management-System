package com.crm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.system.entity.SysDept;
import com.crm.system.mapper.SysDeptMapper;
import com.crm.system.service.ISysDeptService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门服务实现
 *
 * @author CRM
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    @Override
    public List<SysDept> listDeptTree() {
        return baseMapper.selectList(new LambdaQueryWrapper<SysDept>()
                .eq(SysDept::getStatus, 1)
                .orderByAsc(SysDept::getParentId)
                .orderByAsc(SysDept::getOrderNum));
    }

    /**
     * 查询部门及其所有子部门ID列表（含自身）
     * <p>
     * ancestors 字段为逗号分隔的祖级路径（如 "0,1"），通过在首尾补逗号后
     * 判断 ",deptId," 是否出现来精确匹配，避免子串误匹配。
     */
    @Override
    public List<Long> getChildDeptIds(Long deptId) {
        List<SysDept> all = baseMapper.selectList(new LambdaQueryWrapper<SysDept>()
                .select(SysDept::getId, SysDept::getAncestors));
        List<Long> result = new ArrayList<>();
        result.add(deptId);
        String key = "," + deptId + ",";
        for (SysDept dept : all) {
            if (dept.getId().equals(deptId)) {
                continue;
            }
            String ancestors = dept.getAncestors();
            if (ancestors != null && ("," + ancestors + ",").contains(key)) {
                result.add(dept.getId());
            }
        }
        return result;
    }
}
