package com.crm.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.system.entity.SysRole;

import java.util.List;
import java.util.Set;

/**
 * 角色服务接口
 *
 * @author CRM
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 查询用户角色编码集合
     *
     * @param userId 用户ID
     * @return 角色编码集合
     */
    Set<String> getRoleCodesByUserId(Long userId);

    /**
     * 查询用户角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> getRolesByUserId(Long userId);
}
