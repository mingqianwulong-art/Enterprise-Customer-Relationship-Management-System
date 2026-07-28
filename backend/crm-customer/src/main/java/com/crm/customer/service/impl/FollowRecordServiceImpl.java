package com.crm.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.exception.BusinessException;
import com.crm.customer.entity.Customer;
import com.crm.customer.entity.FollowRecord;
import com.crm.customer.mapper.CustomerMapper;
import com.crm.customer.mapper.FollowRecordMapper;
import com.crm.customer.service.IFollowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 跟进记录服务实现
 *
 * @author CRM
 */
@Service
public class FollowRecordServiceImpl extends ServiceImpl<FollowRecordMapper, FollowRecord> implements IFollowRecordService {

    @Autowired
    private CustomerMapper customerMapper;

    /**
     * 按客户ID查跟进记录列表（按时间倒序）
     */
    @Override
    public List<FollowRecord> listByCustomerId(Long customerId) {
        return baseMapper.selectList(
                new LambdaQueryWrapper<FollowRecord>()
                        .eq(FollowRecord::getCustomerId, customerId)
                        .orderByDesc(FollowRecord::getCreateTime));
    }

    /**
     * 新增跟进记录（自动更新客户最后跟进时间）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addFollowRecord(FollowRecord record) {
        if (record.getCustomerId() == null) {
            throw new BusinessException("所属客户不能为空");
        }
        // 插入跟进记录
        int result = baseMapper.insert(record);
        if (result > 0) {
            // 更新客户的最后跟进时间
            Customer update = new Customer();
            update.setId(record.getCustomerId());
            update.setLastFollowTime(LocalDateTime.now());
            customerMapper.updateById(update);
        }
        return result > 0;
    }

    /**
     * 查今日待跟进列表
     */
    @Override
    public List<FollowRecord> listTodayPending() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return baseMapper.selectList(
                new LambdaQueryWrapper<FollowRecord>()
                        .ge(FollowRecord::getNextFollowTime, todayStart)
                        .le(FollowRecord::getNextFollowTime, todayEnd)
                        .orderByAsc(FollowRecord::getNextFollowTime));
    }
}
