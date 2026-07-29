package com.crm.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.exception.BusinessException;
import com.crm.service.entity.ServiceOrder;
import com.crm.service.mapper.ServiceOrderMapper;
import com.crm.service.service.IServiceOrderService;
import com.crm.service.vo.OrderPageDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 工单服务实现
 *
 * @author CRM
 */
@Service
public class ServiceOrderServiceImpl extends ServiceImpl<ServiceOrderMapper, ServiceOrder> implements IServiceOrderService {

    /** 工单已完成状态 */
    private static final int STATUS_DONE = 3;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 分页查询工单
     */
    @Override
    public IPage<ServiceOrder> page(OrderPageDTO dto) {
        Page<ServiceOrder> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<ServiceOrder>()
                .like(dto.getOrderNo() != null && !dto.getOrderNo().isEmpty(),
                        ServiceOrder::getOrderNo, dto.getOrderNo())
                .eq(dto.getCustomerId() != null,
                        ServiceOrder::getCustomerId, dto.getCustomerId())
                .eq(dto.getType() != null,
                        ServiceOrder::getType, dto.getType())
                .eq(dto.getStatus() != null,
                        ServiceOrder::getStatus, dto.getStatus())
                .eq(dto.getPriority() != null,
                        ServiceOrder::getPriority, dto.getPriority())
                .orderByDesc(ServiceOrder::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    /**
     * 工单详情
     */
    @Override
    public ServiceOrder getById(Long id) {
        ServiceOrder order = baseMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        return order;
    }

    /**
     * 新增工单（自动生成工单编号 WO+yyyyMMdd+3位序号）
     */
    @Override
    public boolean addOrder(ServiceOrder order) {
        // 生成工单编号：WO + yyyyMMdd + 3位序号
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String seq = String.format("%03d", System.currentTimeMillis() % 1000);
        order.setOrderNo("WO" + dateStr + seq);
        // 默认状态为待处理（0）
        if (order.getStatus() == null) {
            order.setStatus(0);
        }
        return baseMapper.insert(order) > 0;
    }

    /**
     * 修改工单
     */
    @Override
    public boolean updateOrder(ServiceOrder order) {
        if (order.getId() == null) {
            throw new BusinessException("工单ID不能为空");
        }
        return baseMapper.updateById(order) > 0;
    }

    /**
     * 删除工单（逻辑删除）
     */
    @Override
    public boolean deleteOrder(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    /**
     * 分配工单
     */
    @Override
    public boolean assignOrder(Long id, Long assigneeId, String assigneeName) {
        ServiceOrder order = baseMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        ServiceOrder update = new ServiceOrder();
        update.setId(id);
        update.setAssigneeId(assigneeId);
        update.setAssigneeName(assigneeName);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 修改工单状态（若改为已完成则设置解决时间）
     */
    @Override
    public boolean changeStatus(Long id, Integer status) {
        ServiceOrder order = baseMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        ServiceOrder update = new ServiceOrder();
        update.setId(id);
        update.setStatus(status);
        // 状态改为已完成（3）时记录解决时间
        if (STATUS_DONE == status) {
            update.setResolveTime(LocalDateTime.now());
        }
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 添加满意度评价
     */
    @Override
    public boolean addSatisfaction(Long id, Integer satisfaction, String satisfactionComment) {
        ServiceOrder order = baseMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        ServiceOrder update = new ServiceOrder();
        update.setId(id);
        update.setSatisfaction(satisfaction);
        update.setSatisfactionComment(satisfactionComment);
        return baseMapper.updateById(update) > 0;
    }
}
