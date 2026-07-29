package com.crm.market.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.market.entity.Knowledge;

/**
 * 知识库服务接口
 *
 * @author CRM
 */
public interface IKnowledgeService extends IService<Knowledge> {

    /**
     * 分页查询知识库
     *
     * @param pageNum  当前页
     * @param pageSize 每页条数
     * @param title    标题（模糊查询）
     * @param category 分类
     * @return 分页结果
     */
    IPage<Knowledge> page(Integer pageNum, Integer pageSize, String title, String category);

    /**
     * 知识详情（浏览次数+1）
     *
     * @param id 知识ID
     * @return 知识详情
     */
    Knowledge getById(Long id);

    /**
     * 新增知识
     *
     * @param knowledge 知识信息
     * @return 是否成功
     */
    boolean addKnowledge(Knowledge knowledge);

    /**
     * 修改知识
     *
     * @param knowledge 知识信息
     * @return 是否成功
     */
    boolean updateKnowledge(Knowledge knowledge);

    /**
     * 删除知识
     *
     * @param id 知识ID
     * @return 是否成功
     */
    boolean deleteKnowledge(Long id);
}
