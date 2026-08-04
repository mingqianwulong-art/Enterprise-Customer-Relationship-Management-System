package com.crm.business.controller;

import com.crm.business.entity.Payment;
import com.crm.business.service.IPaymentService;
import com.crm.business.vo.PaymentPageDTO;
import com.crm.common.api.R;
import com.crm.common.constant.Perms;
import com.crm.system.annotation.Log;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 回款控制器
 *
 * @author CRM
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "回款管理")
@RestController
@RequestMapping("/business/payment")
public class PaymentController {

    @Autowired
    private IPaymentService paymentService;

    /**
     * 分页查询回款
     */
    @Operation(summary = "分页查询回款")
    @PreAuthorize("hasAuthority('" + Perms.PAYMENT_LIST + "')")
    @GetMapping("/page")
    public R page(PaymentPageDTO dto) {
        return R.ok(paymentService.page(dto));
    }

    /**
     * 回款详情
     */
    @Operation(summary = "回款详情")
    @PreAuthorize("hasAuthority('" + Perms.PAYMENT_LIST + "')")
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        return R.ok(paymentService.getById(id));
    }

    /**
     * 新增回款记录
     */
    @Operation(summary = "新增回款记录")
    @Log("新增回款记录")
    @PreAuthorize("hasAuthority('" + Perms.PAYMENT_ADD + "')")
    @PostMapping
    public R add(@RequestBody Payment payment) {
        return paymentService.addPayment(payment) ? R.ok("新增回款记录成功") : R.fail("新增回款记录失败");
    }

    /**
     * 修改回款记录
     */
    @Operation(summary = "修改回款记录")
    @PreAuthorize("hasAuthority('" + Perms.PAYMENT_EDIT + "')")
    @PutMapping
    public R update(@RequestBody Payment payment) {
        return paymentService.updatePayment(payment) ? R.ok("修改回款记录成功") : R.fail("修改回款记录失败");
    }

    /**
     * 删除回款记录
     */
    @Operation(summary = "删除回款记录")
    @Log("删除回款记录")
    @PreAuthorize("hasAuthority('" + Perms.PAYMENT_DELETE + "')")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return paymentService.deletePayment(id) ? R.ok("删除回款记录成功") : R.fail("删除回款记录失败");
    }

    /**
     * 确认回款
     */
    @Operation(summary = "确认回款")
    @PreAuthorize("hasAuthority('" + Perms.PAYMENT_CONFIRM + "')")
    @PutMapping("/{id}/confirm")
    public R confirm(@PathVariable Long id,
                     @RequestParam BigDecimal actualAmount,
                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate actualDate) {
        return paymentService.confirmPayment(id, actualAmount, actualDate) ? R.ok("确认回款成功") : R.fail("确认回款失败");
    }
}
