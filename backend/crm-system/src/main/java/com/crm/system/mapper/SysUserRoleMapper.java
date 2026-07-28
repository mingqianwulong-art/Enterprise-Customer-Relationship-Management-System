package com.crm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper
 *
 * @author CRM
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}
