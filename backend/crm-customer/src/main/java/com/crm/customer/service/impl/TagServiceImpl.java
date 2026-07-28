package com.crm.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.exception.BusinessException;
import com.crm.customer.entity.Tag;
import com.crm.customer.mapper.TagMapper;
import com.crm.customer.service.ITagService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 标签服务实现
 *
 * @author CRM
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {

    /**
     * 查所有标签
     */
    @Override
    public List<Tag> listAll() {
        return baseMapper.selectList(
                new LambdaQueryWrapper<Tag>()
                        .orderByAsc(Tag::getTagType)
                        .orderByDesc(Tag::getCreateTime));
    }

    /**
     * 新增标签
     */
    @Override
    public boolean addTag(Tag tag) {
        // 标签名查重
        Long count = baseMapper.selectCount(
                new LambdaQueryWrapper<Tag>()
                        .eq(Tag::getTagName, tag.getTagName()));
        if (count > 0) {
            throw new BusinessException("标签名称已存在");
        }
        return baseMapper.insert(tag) > 0;
    }

    /**
     * 修改标签
     */
    @Override
    public boolean updateTag(Tag tag) {
        if (tag.getId() == null) {
            throw new BusinessException("标签ID不能为空");
        }
        return baseMapper.updateById(tag) > 0;
    }

    /**
     * 删除标签（逻辑删除）
     */
    @Override
    public boolean deleteTag(Long id) {
        return baseMapper.deleteById(id) > 0;
    }
}
