package com.crm.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.customer.entity.CustomerTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户标签关联Mapper
 *
 * @author CRM
 */
@Mapper
public interface CustomerTagMapper extends BaseMapper<CustomerTag> {
}
