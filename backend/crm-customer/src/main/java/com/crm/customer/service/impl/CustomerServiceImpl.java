package com.crm.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.exception.BusinessException;
import com.crm.common.security.SecurityUtils;
import com.crm.customer.entity.Contact;
import com.crm.customer.entity.Customer;
import com.crm.customer.entity.CustomerTag;
import com.crm.customer.entity.FollowRecord;
import com.crm.customer.entity.Tag;
import com.crm.customer.mapper.ContactMapper;
import com.crm.customer.mapper.CustomerMapper;
import com.crm.customer.mapper.CustomerTagMapper;
import com.crm.customer.mapper.FollowRecordMapper;
import com.crm.customer.mapper.TagMapper;
import com.crm.system.service.DataPermissionService;
import com.crm.customer.service.ICustomerService;
import com.crm.customer.vo.CustomerPageDTO;
import com.crm.customer.vo.CustomerVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户服务实现
 *
 * @author CRM
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements ICustomerService {

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private CustomerTagMapper customerTagMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private FollowRecordMapper followRecordMapper;

    @Autowired
    private DataPermissionService dataPermissionService;

    /**
     * 分页查询客户
     */
    @Override
    public IPage<Customer> page(CustomerPageDTO dto) {
        Page<Customer> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<Customer>()
                .like(dto.getName() != null && !dto.getName().isEmpty(),
                        Customer::getName, dto.getName())
                .eq(dto.getIndustry() != null && !dto.getIndustry().isEmpty(),
                        Customer::getIndustry, dto.getIndustry())
                .eq(dto.getRegion() != null && !dto.getRegion().isEmpty(),
                        Customer::getRegion, dto.getRegion())
                .eq(dto.getOwnerId() != null,
                        Customer::getOwnerId, dto.getOwnerId())
                .eq(dto.getLevel() != null,
                        Customer::getCustomerLevel, dto.getLevel())
                .orderByDesc(Customer::getCreateTime);
        // 应用数据权限：按当前用户 dataScope 过滤可见的 owner_id
        List<Long> visibleOwnerIds = dataPermissionService.getVisibleOwnerIds();
        if (visibleOwnerIds != null) {
            wrapper.in(Customer::getOwnerId, visibleOwnerIds);
        }
        return baseMapper.selectPage(page, wrapper);
    }

    /**
     * 客户详情（含联系人、标签、跟进记录）
     */
    @Override
    public CustomerVO getDetail(Long id) {
        Customer customer = baseMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        // 非公海客户需校验数据权限（公海客户允许所有人查看以便领取）
        Integer inPool = customer.getInPool();
        if (inPool == null || inPool == 0) {
            List<Long> visibleOwnerIds = dataPermissionService.getVisibleOwnerIds();
            if (visibleOwnerIds != null
                    && (customer.getOwnerId() == null || !visibleOwnerIds.contains(customer.getOwnerId()))) {
                throw new BusinessException("无权查看该客户");
            }
        }

        CustomerVO vo = new CustomerVO();
        BeanUtils.copyProperties(customer, vo);

        // 查联系人
        List<Contact> contacts = contactMapper.selectList(
                new LambdaQueryWrapper<Contact>()
                        .eq(Contact::getCustomerId, id));
        vo.setContacts(contacts);

        // 查标签（通过关联表 cus_customer_tag）
        List<CustomerTag> customerTags = customerTagMapper.selectList(
                new LambdaQueryWrapper<CustomerTag>()
                        .eq(CustomerTag::getCustomerId, id));
        if (!customerTags.isEmpty()) {
            List<Long> tagIds = customerTags.stream()
                    .map(CustomerTag::getTagId)
                    .collect(Collectors.toList());
            List<Tag> tags = tagMapper.selectList(
                    new LambdaQueryWrapper<Tag>()
                            .in(Tag::getId, tagIds));
            vo.setTags(tags);
        }

        // 查跟进记录（最近5条，按时间倒序）
        List<FollowRecord> followRecords = followRecordMapper.selectList(
                new LambdaQueryWrapper<FollowRecord>()
                        .eq(FollowRecord::getCustomerId, id)
                        .orderByDesc(FollowRecord::getCreateTime)
                        .last("LIMIT 5"));
        vo.setFollowRecords(followRecords);

        return vo;
    }

    /**
     * 新增客户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCustomer(Customer customer) {
        // 信用代码为空时设为null，避免空字符串触发唯一约束冲突
        if (customer.getCreditCode() != null && customer.getCreditCode().isEmpty()) {
            customer.setCreditCode(null);
        }
        // 信用代码查重（非空时才校验）
        if (customer.getCreditCode() != null && !customer.getCreditCode().isEmpty()) {
            Long count = baseMapper.selectCount(
                    new LambdaQueryWrapper<Customer>()
                            .eq(Customer::getCreditCode, customer.getCreditCode()));
            if (count > 0) {
                throw new BusinessException("信用代码已存在，请勿重复添加");
            }
        }
        // 默认不在公海
        if (customer.getInPool() == null) {
            customer.setInPool(0);
        }
        return baseMapper.insert(customer) > 0;
    }

    /**
     * 修改客户
     */
    @Override
    public boolean updateCustomer(Customer customer) {
        if (customer.getId() == null) {
            throw new BusinessException("客户ID不能为空");
        }
        return baseMapper.updateById(customer) > 0;
    }

    /**
     * 删除客户（逻辑删除）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCustomer(Long id) {
        // 删除关联联系人
        contactMapper.delete(new LambdaQueryWrapper<Contact>()
                .eq(Contact::getCustomerId, id));
        // 删除标签关联
        customerTagMapper.delete(new LambdaQueryWrapper<CustomerTag>()
                .eq(CustomerTag::getCustomerId, id));
        // 删除跟进记录
        followRecordMapper.delete(new LambdaQueryWrapper<FollowRecord>()
                .eq(FollowRecord::getCustomerId, id));
        return baseMapper.deleteById(id) > 0;
    }

    /**
     * 领取客户（从公海）
     * 使用条件更新（WHERE in_pool=1）实现乐观锁，防止并发领取
     */
    @Override
    public boolean claimCustomer(Long id, Long userId) {
        Customer customer = baseMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        if (customer.getInPool() == null || customer.getInPool() == 0) {
            throw new BusinessException("该客户不在公海中，无法领取");
        }
        // 乐观锁：条件更新 WHERE in_pool=1，并发下只有一方能成功
        LambdaUpdateWrapper<Customer> wrapper = new LambdaUpdateWrapper<Customer>()
                .eq(Customer::getId, id)
                .eq(Customer::getInPool, 1)
                .set(Customer::getOwnerId, userId)
                .set(Customer::getInPool, 0);
        int rows = baseMapper.update(null, wrapper);
        if (rows == 0) {
            throw new BusinessException("客户已被他人领取，请刷新后重试");
        }
        return true;
    }

    /**
     * 退回公海（校验归属：仅负责人本人或有数据权限的用户可操作）
     */
    @Override
    public boolean releaseToPool(Long id) {
        Customer customer = baseMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        // 校验归属：负责人本人 或 该客户在当前用户数据权限范围内
        List<Long> visibleOwnerIds = dataPermissionService.getVisibleOwnerIds();
        Long currentUserId = SecurityUtils.getCurrentUserIdRequired();
        boolean isOwner = customer.getOwnerId() != null && customer.getOwnerId().equals(currentUserId);
        boolean hasDataScope = visibleOwnerIds == null
                || (customer.getOwnerId() != null && visibleOwnerIds.contains(customer.getOwnerId()));
        if (!isOwner && !hasDataScope) {
            throw new BusinessException("无权操作：仅客户负责人或有数据权限的用户可退回公海");
        }
        Customer update = new Customer();
        update.setId(id);
        update.setInPool(1);
        update.setOwnerId(null);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 公海池分页查询
     */
    @Override
    public IPage<Customer> poolPage(CustomerPageDTO dto) {
        Page<Customer> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<Customer>()
                .eq(Customer::getInPool, 1)
                .like(dto.getName() != null && !dto.getName().isEmpty(),
                        Customer::getName, dto.getName())
                .eq(dto.getIndustry() != null && !dto.getIndustry().isEmpty(),
                        Customer::getIndustry, dto.getIndustry())
                .orderByDesc(Customer::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    /**
     * 公海池自动回收
     * 查询超过指定天数未跟进的已分配客户，批量转入公海池
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int autoRecycleToPool(int days) {
        LocalDateTime deadline = LocalDateTime.now().minusDays(days);
        // 查询需要回收的客户：非公海、有负责人、超过阈值未跟进
        // last_follow_time 为空时按 create_time 判断（从未跟进的客户）
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<Customer>()
                .eq(Customer::getInPool, 0)
                .isNotNull(Customer::getOwnerId)
                .and(w -> w
                        .lt(Customer::getLastFollowTime, deadline)
                        .or()
                        .nested(n -> n.isNull(Customer::getLastFollowTime)
                                .lt(Customer::getCreateTime, deadline)));
        List<Customer> customers = baseMapper.selectList(wrapper);
        if (customers.isEmpty()) {
            return 0;
        }
        // 批量转入公海：清空负责人、标记为公海
        List<Long> ids = customers.stream()
                .map(Customer::getId)
                .collect(Collectors.toList());
        LambdaUpdateWrapper<Customer> updateWrapper = new LambdaUpdateWrapper<Customer>()
                .in(Customer::getId, ids)
                .set(Customer::getInPool, 1)
                .set(Customer::getOwnerId, null);
        return baseMapper.update(null, updateWrapper);
    }
}
