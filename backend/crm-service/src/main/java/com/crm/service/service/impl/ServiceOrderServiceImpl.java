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
import com.crm.system.service.DataPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private DataPermissionService dataPermissionService;

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
        // 数据权限过滤（按受理人 assigneeId）
        List<Long> visibleOwnerIds = dataPermissionService.getVisibleOwnerIds();
        if (visibleOwnerIds != null) {
            wrapper.in(ServiceOrder::getAssigneeId, visibleOwnerIds);
        }
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
        // 生成工单编号：WO + yyyyMMdd + 3位序号（查询当天已有数量+1）
        String today = LocalDateTime.now().format(DATE_FORMATTER);
        String prefix = "WO" + today;
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(ServiceOrder::getOrderNo, prefix);
        long count = baseMapper.selectCount(wrapper);
        order.setOrderNo(prefix + String.format("%03d", count + 1));
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

    /** 工单类型名称映射 */
    private static final Map<Integer, String> TYPE_NAMES = new HashMap<>();
    static {
        TYPE_NAMES.put(1, "产品咨询");
        TYPE_NAMES.put(2, "售后维修");
        TYPE_NAMES.put(3, "退换货");
        TYPE_NAMES.put(4, "安装调试");
        TYPE_NAMES.put(5, "投诉建议");
        TYPE_NAMES.put(6, "其他");
    }

    /**
     * 问题反向溯源：高频问题类型统计
     */
    @Override
    public List<Map<String, Object>> hotProblemStats(int days) {
        // 查询指定天数内的工单
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<ServiceOrder>()
                .ge(ServiceOrder::getCreateTime, startTime)
                .select(ServiceOrder::getType);
        List<ServiceOrder> orders = baseMapper.selectList(wrapper);

        // 按类型分组统计
        Map<Integer, Long> typeCountMap = orders.stream()
                .filter(o -> o.getType() != null)
                .collect(Collectors.groupingBy(ServiceOrder::getType, Collectors.counting()));

        long total = orders.size();
        List<Map<String, Object>> result = new ArrayList<>();
        typeCountMap.forEach((type, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("type", type);
            item.put("typeName", TYPE_NAMES.getOrDefault(type, "未知"));
            item.put("count", count);
            item.put("percentage", total > 0 ? String.format("%.1f%%", count * 100.0 / total) : "0.0%");
            result.add(item);
        });
        // 按次数降序
        result.sort((a, b) -> Long.compare((Long) b.get("count"), (Long) a.get("count")));
        return result;
    }
}
