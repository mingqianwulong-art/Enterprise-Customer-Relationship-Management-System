package com.crm.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.service.entity.ServiceRecord;
import com.crm.service.vo.RecordPageDTO;

import java.util.List;

/**
 * 售后记录服务接口
 *
 * @author CRM
 */
public interface IServiceRecordService extends IService<ServiceRecord> {

    /**
     * 分页查询售后记录
     *
     * @param dto 分页查询条件
     * @return 分页结果
     */
    IPage<ServiceRecord> page(RecordPageDTO dto);

    /**
     * 售后记录详情
     *
     * @param id 售后记录ID
     * @return 售后记录信息
     */
    ServiceRecord getById(Long id);

    /**
     * 新增售后记录
     *
     * @param record 售后记录信息
     * @return 是否成功
     */
    boolean addRecord(ServiceRecord record);

    /**
     * 修改售后记录
     *
     * @param record 售后记录信息
     * @return 是否成功
     */
    boolean updateRecord(ServiceRecord record);

    /**
     * 删除售后记录（逻辑删除）
     *
     * @param id 售后记录ID
     * @return 是否成功
     */
    boolean deleteRecord(Long id);

    /**
     * 根据工单ID查询售后记录列表
     *
     * @param orderId 工单ID
     * @return 售后记录列表
     */
    List<ServiceRecord> listByOrderId(Long orderId);

    /**
     * 根据客户ID查询售后记录列表
     *
     * @param customerId 客户ID
     * @return 售后记录列表
     */
    List<ServiceRecord> listByCustomerId(Long customerId);
}
