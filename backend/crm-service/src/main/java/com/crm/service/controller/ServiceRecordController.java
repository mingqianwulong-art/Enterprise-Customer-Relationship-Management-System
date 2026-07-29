package com.crm.service.controller;

import com.crm.common.api.R;
import com.crm.service.entity.ServiceRecord;
import com.crm.service.service.IServiceRecordService;
import com.crm.service.vo.RecordPageDTO;
import com.crm.system.annotation.Log;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 售后记录控制器
 *
 * @author CRM
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "售后记录")
@RestController
@RequestMapping("/service/record")
public class ServiceRecordController {

    @Autowired
    private IServiceRecordService recordService;

    /**
     * 分页查询售后记录
     */
    @Operation(summary = "分页查询售后记录")
    @GetMapping("/page")
    public R page(RecordPageDTO dto) {
        return R.ok(recordService.page(dto));
    }

    /**
     * 售后记录详情
     */
    @Operation(summary = "售后记录详情")
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        return R.ok(recordService.getById(id));
    }

    /**
     * 新增售后记录
     */
    @Operation(summary = "新增售后记录")
    @Log("新增售后记录")
    @PostMapping
    public R add(@RequestBody ServiceRecord record) {
        return recordService.addRecord(record) ? R.ok("新增售后记录成功") : R.fail("新增售后记录失败");
    }

    /**
     * 修改售后记录
     */
    @Operation(summary = "修改售后记录")
    @PutMapping
    public R update(@RequestBody ServiceRecord record) {
        return recordService.updateRecord(record) ? R.ok("修改售后记录成功") : R.fail("修改售后记录失败");
    }

    /**
     * 删除售后记录
     */
    @Operation(summary = "删除售后记录")
    @Log("删除售后记录")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return recordService.deleteRecord(id) ? R.ok("删除售后记录成功") : R.fail("删除售后记录失败");
    }

    /**
     * 根据工单ID查询售后记录列表
     */
    @Operation(summary = "根据工单ID查询售后记录")
    @GetMapping("/order/{orderId}")
    public R listByOrderId(@PathVariable Long orderId) {
        return R.ok(recordService.listByOrderId(orderId));
    }

    /**
     * 根据客户ID查询售后记录列表
     */
    @Operation(summary = "根据客户ID查询售后记录")
    @GetMapping("/customer/{customerId}")
    public R listByCustomerId(@PathVariable Long customerId) {
        return R.ok(recordService.listByCustomerId(customerId));
    }
}
