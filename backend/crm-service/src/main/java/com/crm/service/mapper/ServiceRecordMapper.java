package com.crm.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.service.entity.ServiceRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 售后记录Mapper
 *
 * @author CRM
 */
@Mapper
public interface ServiceRecordMapper extends BaseMapper<ServiceRecord> {
}
