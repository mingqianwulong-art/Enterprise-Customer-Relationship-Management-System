package com.crm.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.exception.BusinessException;
import com.crm.customer.entity.Contact;
import com.crm.customer.mapper.ContactMapper;
import com.crm.customer.service.IContactService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 联系人服务实现
 *
 * @author CRM
 */
@Service
public class ContactServiceImpl extends ServiceImpl<ContactMapper, Contact> implements IContactService {

    /**
     * 按客户ID查询联系人列表
     */
    @Override
    public List<Contact> listByCustomerId(Long customerId) {
        return baseMapper.selectList(
                new LambdaQueryWrapper<Contact>()
                        .eq(Contact::getCustomerId, customerId)
                        .orderByDesc(Contact::getIsPrimary));
    }

    /**
     * 新增联系人
     */
    @Override
    public boolean addContact(Contact contact) {
        if (contact.getCustomerId() == null) {
            throw new BusinessException("所属客户不能为空");
        }
        return baseMapper.insert(contact) > 0;
    }

    /**
     * 修改联系人
     */
    @Override
    public boolean updateContact(Contact contact) {
        if (contact.getId() == null) {
            throw new BusinessException("联系人ID不能为空");
        }
        return baseMapper.updateById(contact) > 0;
    }

    /**
     * 删除联系人（逻辑删除）
     */
    @Override
    public boolean deleteContact(Long id) {
        return baseMapper.deleteById(id) > 0;
    }
}
