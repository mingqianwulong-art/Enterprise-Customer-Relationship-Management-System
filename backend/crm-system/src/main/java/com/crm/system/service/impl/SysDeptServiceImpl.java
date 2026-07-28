package com.crm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.system.entity.SysDept;
import com.crm.system.mapper.SysDeptMapper;
import com.crm.system.service.ISysDeptService;
import org.springframework.stereotype.Service;

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
}
