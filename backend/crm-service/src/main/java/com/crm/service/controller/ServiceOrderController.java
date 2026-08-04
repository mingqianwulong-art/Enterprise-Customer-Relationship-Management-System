package com.crm.service.controller;

import com.crm.common.api.R;
import com.crm.common.constant.Perms;
import com.crm.service.entity.ServiceOrder;
import com.crm.service.service.IServiceOrderService;
import com.crm.service.vo.OrderPageDTO;
import com.crm.system.annotation.Log;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工单控制器
 *
 * @author CRM
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "工单管理")
@RestController
@RequestMapping("/service/order")
public class ServiceOrderController {

    @Autowired
    private IServiceOrderService orderService;

    /**
     * 分页查询工单
     */
    @Operation(summary = "分页查询工单")
    @PreAuthorize("hasAuthority('" + Perms.SERVICE_ORDER_LIST + "')")
    @GetMapping("/page")
    public R page(OrderPageDTO dto) {
        return R.ok(orderService.page(dto));
    }

    /**
     * 工单详情
     */
    @Operation(summary = "工单详情")
    @PreAuthorize("hasAuthority('" + Perms.SERVICE_ORDER_LIST + "')")
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        return R.ok(orderService.getById(id));
    }

    /**
     * 新增工单
     */
    @Operation(summary = "新增工单")
    @Log("新增工单")
    @PreAuthorize("hasAuthority('" + Perms.SERVICE_ORDER_ADD + "')")
    @PostMapping
    public R add(@RequestBody ServiceOrder order) {
        return orderService.addOrder(order) ? R.ok("新增工单成功") : R.fail("新增工单失败");
    }

    /**
     * 修改工单
     */
    @Operation(summary = "修改工单")
    @PreAuthorize("hasAuthority('" + Perms.SERVICE_ORDER_EDIT + "')")
    @PutMapping
    public R update(@RequestBody ServiceOrder order) {
        return orderService.updateOrder(order) ? R.ok("修改工单成功") : R.fail("修改工单失败");
    }

    /**
     * 删除工单
     */
    @Operation(summary = "删除工单")
    @Log("删除工单")
    @PreAuthorize("hasAuthority('" + Perms.SERVICE_ORDER_DELETE + "')")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return orderService.deleteOrder(id) ? R.ok("删除工单成功") : R.fail("删除工单失败");
    }

    /**
     * 分配工单
     */
    @Operation(summary = "分配工单")
    @PreAuthorize("hasAuthority('" + Perms.SERVICE_ORDER_ASSIGN + "')")
    @PutMapping("/{id}/assign")
    public R assign(@PathVariable Long id,
                    @RequestParam Long assigneeId,
                    @RequestParam String assigneeName) {
        return orderService.assignOrder(id, assigneeId, assigneeName) ? R.ok("分配工单成功") : R.fail("分配工单失败");
    }

    /**
     * 修改工单状态
     */
    @Operation(summary = "修改工单状态")
    @PreAuthorize("hasAuthority('" + Perms.SERVICE_ORDER_STATUS + "')")
    @PutMapping("/{id}/status")
    public R changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        return orderService.changeStatus(id, status) ? R.ok("修改状态成功") : R.fail("修改状态失败");
    }

    /**
     * 添加满意度评价
     */
    @Operation(summary = "添加满意度评价")
    @PreAuthorize("hasAuthority('" + Perms.SERVICE_ORDER_SATISFACTION + "')")
    @PutMapping("/{id}/satisfaction")
    public R addSatisfaction(@PathVariable Long id,
                             @RequestParam Integer satisfaction,
                             @RequestParam(required = false) String satisfactionComment) {
        return orderService.addSatisfaction(id, satisfaction, satisfactionComment) ? R.ok("评价成功") : R.fail("评价失败");
    }

    /**
     * 问题反向溯源：高频问题类型统计
     * <p>
     * 统计指定时间范围内各工单类型出现次数，按频次降序返回，用于推送至产品部门改进
     *
     * @param days 统计天数（默认30天）
     */
    @Operation(summary = "问题反向溯源-高频问题统计")
    @PreAuthorize("hasAuthority('" + Perms.SERVICE_ORDER_HOT_PROBLEMS + "')")
    @GetMapping("/hot-problems")
    public R hotProblemStats(@RequestParam(defaultValue = "30") int days) {
        return R.ok(orderService.hotProblemStats(days));
    }
}
