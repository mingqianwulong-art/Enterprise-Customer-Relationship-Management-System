package com.crm.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.business.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 回款Mapper
 *
 * @author CRM
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}
