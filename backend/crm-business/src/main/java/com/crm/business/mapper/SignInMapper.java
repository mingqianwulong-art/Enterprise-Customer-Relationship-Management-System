package com.crm.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.business.entity.SignIn;
import org.apache.ibatis.annotations.Mapper;

/**
 * 外勤签到 Mapper
 *
 * @author CRM
 */
@Mapper
public interface SignInMapper extends BaseMapper<SignIn> {
}
