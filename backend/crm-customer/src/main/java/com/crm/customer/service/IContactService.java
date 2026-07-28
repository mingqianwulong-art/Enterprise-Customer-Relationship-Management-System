package com.crm.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.customer.entity.Contact;

import java.util.List;

/**
 * 联系人服务接口
 *
 * @author CRM
 */
public interface IContactService extends IService<Contact> {

    /**
     * 按客户ID查询联系人列表
     *
     * @param customerId 客户ID
     * @return 联系人列表
     */
    List<Contact> listByCustomerId(Long customerId);

    /**
     * 新增联系人
     *
     * @param contact 联系人信息
     * @return 是否成功
     */
    boolean addContact(Contact contact);

    /**
     * 修改联系人
     *
     * @param contact 联系人信息
     * @return 是否成功
     */
    boolean updateContact(Contact contact);

    /**
     * 删除联系人（逻辑删除）
     *
     * @param id 联系人ID
     * @return 是否成功
     */
    boolean deleteContact(Long id);
}
