package com.crm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.system.entity.SysRole;
import com.crm.system.entity.SysUserRole;
import com.crm.system.mapper.SysRoleMapper;
import com.crm.system.mapper.SysUserRoleMapper;
import com.crm.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 *
 * @author CRM
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Override
    public Set<String> getRoleCodesByUserId(Long userId) {
        List<SysRole> roles = getRolesByUserId(userId);
        return roles.stream().map(SysRole::getRoleCode).collect(Collectors.toSet());
    }

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        // 查询用户角色关联
        List<SysUserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).distinct().collect(Collectors.toList());
        // 查询启用的角色
        List<SysRole> roles = baseMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .in(SysRole::getId, roleIds)
                .eq(SysRole::getStatus, 1));
        return roles;
    }
}
