package com.crm.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.customer.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 *
 * @author CRM
 */
public interface ITagService extends IService<Tag> {

    /**
     * 查所有标签
     *
     * @return 标签列表
     */
    List<Tag> listAll();

    /**
     * 新增标签
     *
     * @param tag 标签信息
     * @return 是否成功
     */
    boolean addTag(Tag tag);

    /**
     * 修改标签
     *
     * @param tag 标签信息
     * @return 是否成功
     */
    boolean updateTag(Tag tag);

    /**
     * 删除标签（逻辑删除）
     *
     * @param id 标签ID
     * @return 是否成功
     */
    boolean deleteTag(Long id);
}
