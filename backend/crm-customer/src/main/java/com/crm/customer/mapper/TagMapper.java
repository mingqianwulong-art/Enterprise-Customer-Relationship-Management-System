package com.crm.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.customer.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签Mapper
 *
 * @author CRM
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}
