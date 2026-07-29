package com.crm.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.exception.BusinessException;
import com.crm.market.entity.Knowledge;
import com.crm.market.mapper.KnowledgeMapper;
import com.crm.market.service.IKnowledgeService;
import org.springframework.stereotype.Service;

/**
 * 知识库服务实现
 *
 * @author CRM
 */
@Service
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeMapper, Knowledge> implements IKnowledgeService {

    /**
     * 分页查询知识库
     */
    @Override
    public IPage<Knowledge> page(Integer pageNum, Integer pageSize, String title, String category) {
        Page<Knowledge> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Knowledge> wrapper = new LambdaQueryWrapper<Knowledge>()
                .like(title != null && !title.isEmpty(),
                        Knowledge::getTitle, title)
                .eq(category != null && !category.isEmpty(),
                        Knowledge::getCategory, category)
                .orderByDesc(Knowledge::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    /**
     * 知识详情（浏览次数+1）
     */
    @Override
    public Knowledge getById(Long id) {
        Knowledge knowledge = baseMapper.selectById(id);
        if (knowledge == null) {
            throw new BusinessException("知识库内容不存在");
        }
        // 浏览次数+1
        Knowledge update = new Knowledge();
        update.setId(id);
        update.setViewCount(knowledge.getViewCount() == null ? 1 : knowledge.getViewCount() + 1);
        baseMapper.updateById(update);
        // 返回最新浏览次数
        knowledge.setViewCount(update.getViewCount());
        return knowledge;
    }

    /**
     * 新增知识
     */
    @Override
    public boolean addKnowledge(Knowledge knowledge) {
        if (knowledge.getViewCount() == null) {
            knowledge.setViewCount(0);
        }
        return baseMapper.insert(knowledge) > 0;
    }

    /**
     * 修改知识
     */
    @Override
    public boolean updateKnowledge(Knowledge knowledge) {
        if (knowledge.getId() == null) {
            throw new BusinessException("知识ID不能为空");
        }
        return baseMapper.updateById(knowledge) > 0;
    }

    /**
     * 删除知识（逻辑删除）
     */
    @Override
    public boolean deleteKnowledge(Long id) {
        return baseMapper.deleteById(id) > 0;
    }
}
