package com.crm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 *
 * @author CRM
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
