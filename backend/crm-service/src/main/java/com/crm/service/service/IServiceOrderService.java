package com.crm.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.service.entity.ServiceOrder;
import com.crm.service.vo.OrderPageDTO;

/**
 * 工单服务接口
 *
 * @author CRM
 */
public interface IServiceOrderService extends IService<ServiceOrder> {

    /**
     * 分页查询工单
     *
     * @param dto 分页查询条件
     * @return 分页结果
     */
    IPage<ServiceOrder> page(OrderPageDTO dto);

    /**
     * 工单详情
     *
     * @param id 工单ID
     * @return 工单信息
     */
    ServiceOrder getById(Long id);

    /**
     * 新增工单
     *
     * @param order 工单信息
     * @return 是否成功
     */
    boolean addOrder(ServiceOrder order);

    /**
     * 修改工单
     *
     * @param order 工单信息
     * @return 是否成功
     */
    boolean updateOrder(ServiceOrder order);

    /**
     * 删除工单（逻辑删除）
     *
     * @param id 工单ID
     * @return 是否成功
     */
    boolean deleteOrder(Long id);

    /**
     * 分配工单
     *
     * @param id           工单ID
     * @param assigneeId   处理人ID
     * @param assigneeName 处理人姓名
     * @return 是否成功
     */
    boolean assignOrder(Long id, Long assigneeId, String assigneeName);

    /**
     * 修改工单状态
     *
     * @param id     工单ID
     * @param status 状态
     * @return 是否成功
     */
    boolean changeStatus(Long id, Integer status);

    /**
     * 添加满意度评价
     *
     * @param id                  工单ID
     * @param satisfaction        满意度评分
     * @param satisfactionComment 满意度评价
     * @return 是否成功
     */
    boolean addSatisfaction(Long id, Integer satisfaction, String satisfactionComment);
}
