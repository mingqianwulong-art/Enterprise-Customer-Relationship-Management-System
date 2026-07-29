package com.crm.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.service.entity.ServiceOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工单Mapper
 *
 * @author CRM
 */
@Mapper
public interface ServiceOrderMapper extends BaseMapper<ServiceOrder> {
}
