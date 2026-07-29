package com.crm.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.business.entity.Opportunity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商机Mapper
 *
 * @author CRM
 */
@Mapper
public interface OpportunityMapper extends BaseMapper<Opportunity> {
}
