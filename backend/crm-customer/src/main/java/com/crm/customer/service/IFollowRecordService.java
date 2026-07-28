package com.crm.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.customer.entity.FollowRecord;

import java.util.List;

/**
 * 跟进记录服务接口
 *
 * @author CRM
 */
public interface IFollowRecordService extends IService<FollowRecord> {

    /**
     * 按客户ID查跟进记录列表（按时间倒序）
     *
     * @param customerId 客户ID
     * @return 跟进记录列表
     */
    List<FollowRecord> listByCustomerId(Long customerId);

    /**
     * 新增跟进记录（自动更新客户最后跟进时间）
     *
     * @param record 跟进记录
     * @return 是否成功
     */
    boolean addFollowRecord(FollowRecord record);

    /**
     * 查今日待跟进列表
     *
     * @return 今日待跟进记录列表
     */
    List<FollowRecord> listTodayPending();
}
