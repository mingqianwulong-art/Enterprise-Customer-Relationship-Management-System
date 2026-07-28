package com.crm.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.customer.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户Mapper
 *
 * @author CRM
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
