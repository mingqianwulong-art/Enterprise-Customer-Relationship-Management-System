package com.crm.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.exception.BusinessException;
import com.crm.service.entity.ServiceRecord;
import com.crm.service.mapper.ServiceRecordMapper;
import com.crm.service.service.IServiceRecordService;
import com.crm.service.vo.RecordPageDTO;
import com.crm.system.service.DataPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 售后记录服务实现
 *
 * @author CRM
 */
@Service
public class ServiceRecordServiceImpl extends ServiceImpl<ServiceRecordMapper, ServiceRecord> implements IServiceRecordService {

    @Autowired
    private DataPermissionService dataPermissionService;

    /**
     * 分页查询售后记录
     */
    @Override
    public IPage<ServiceRecord> page(RecordPageDTO dto) {
        Page<ServiceRecord> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<ServiceRecord> wrapper = new LambdaQueryWrapper<ServiceRecord>()
                .eq(dto.getCustomerId() != null,
                        ServiceRecord::getCustomerId, dto.getCustomerId())
                .eq(dto.getType() != null,
                        ServiceRecord::getType, dto.getType())
                .eq(dto.getOrderId() != null,
                        ServiceRecord::getOrderId, dto.getOrderId())
                .orderByDesc(ServiceRecord::getCreateTime);
        // 数据权限过滤（按处理人 handlerId）
        List<Long> visibleOwnerIds = dataPermissionService.getVisibleOwnerIds();
        if (visibleOwnerIds != null) {
            wrapper.in(ServiceRecord::getHandlerId, visibleOwnerIds);
        }
        return baseMapper.selectPage(page, wrapper);
    }

    /**
     * 售后记录详情
     */
    @Override
    public ServiceRecord getById(Long id) {
        ServiceRecord record = baseMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("售后记录不存在");
        }
        return record;
    }

    /**
     * 新增售后记录
     */
    @Override
    public boolean addRecord(ServiceRecord record) {
        return baseMapper.insert(record) > 0;
    }

    /**
     * 修改售后记录
     */
    @Override
    public boolean updateRecord(ServiceRecord record) {
        if (record.getId() == null) {
            throw new BusinessException("售后记录ID不能为空");
        }
        return baseMapper.updateById(record) > 0;
    }

    /**
     * 删除售后记录（逻辑删除）
     */
    @Override
    public boolean deleteRecord(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    /**
     * 根据工单ID查询售后记录列表
     */
    @Override
    public List<ServiceRecord> listByOrderId(Long orderId) {
        LambdaQueryWrapper<ServiceRecord> wrapper = new LambdaQueryWrapper<ServiceRecord>()
                .eq(ServiceRecord::getOrderId, orderId)
                .orderByDesc(ServiceRecord::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据客户ID查询售后记录列表
     */
    @Override
    public List<ServiceRecord> listByCustomerId(Long customerId) {
        LambdaQueryWrapper<ServiceRecord> wrapper = new LambdaQueryWrapper<ServiceRecord>()
                .eq(ServiceRecord::getCustomerId, customerId)
                .orderByDesc(ServiceRecord::getCreateTime);
        return baseMapper.selectList(wrapper);
    }
}
