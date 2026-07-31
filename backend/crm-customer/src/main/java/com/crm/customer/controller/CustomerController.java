package com.crm.customer.controller;

import com.crm.common.api.R;
import com.crm.common.constant.Perms;
import com.crm.common.security.SecurityUtils;
import com.crm.customer.entity.Customer;
import com.crm.customer.service.ICustomerService;
import com.crm.customer.vo.CustomerPageDTO;
import com.crm.system.annotation.Log;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户控制器
 *
 * @author CRM
 */
@Tag(name = "客户管理")
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private ICustomerService customerService;

    /**
     * 分页查询客户
     */
    @Operation(summary = "分页查询客户")
    @GetMapping("/page")
    public R page(CustomerPageDTO dto) {
        return R.ok(customerService.page(dto));
    }

    /**
     * 客户详情（含联系人、标签、跟进记录）
     */
    @Operation(summary = "客户详情（含联系人、标签、跟进记录）")
    @GetMapping("/{id}")
    public R getDetail(@PathVariable Long id) {
        return R.ok(customerService.getDetail(id));
    }

    /**
     * 新增客户
     */
    @Operation(summary = "新增客户")
    @Log("新增客户")
    @PreAuthorize("hasAuthority('" + Perms.CUSTOMER_ADD + "')")
    @PostMapping
    public R add(@RequestBody Customer customer) {
        return customerService.addCustomer(customer) ? R.ok("新增客户成功") : R.fail("新增客户失败");
    }

    /**
     * 修改客户
     */
    @Operation(summary = "修改客户")
    @PreAuthorize("hasAuthority('" + Perms.CUSTOMER_EDIT + "')")
    @PutMapping
    public R update(@RequestBody Customer customer) {
        return customerService.updateCustomer(customer) ? R.ok("修改客户成功") : R.fail("修改客户失败");
    }

    /**
     * 删除客户（逻辑删除）
     */
    @Operation(summary = "删除客户")
    @Log("删除客户")
    @PreAuthorize("hasAuthority('" + Perms.CUSTOMER_DELETE + "')")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return customerService.deleteCustomer(id) ? R.ok("删除客户成功") : R.fail("删除客户失败");
    }

    /**
     * 领取客户（从公海）
     */
    @Operation(summary = "领取客户（从公海）")
    @Log("领取客户")
    @PreAuthorize("hasAuthority('" + Perms.CUSTOMER_EDIT + "')")
    @PutMapping("/{id}/claim")
    public R claim(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserIdRequired();
        return customerService.claimCustomer(id, currentUserId) ? R.ok("领取客户成功") : R.fail("领取客户失败");
    }

    /**
     * 退回公海
     */
    @Operation(summary = "退回公海")
    @PreAuthorize("hasAuthority('" + Perms.CUSTOMER_EDIT + "')")
    @PutMapping("/{id}/release")
    public R release(@PathVariable Long id) {
        return customerService.releaseToPool(id) ? R.ok("退回公海成功") : R.fail("退回公海失败");
    }

    /**
     * 公海池分页查询
     */
    @Operation(summary = "公海池分页查询")
    @GetMapping("/pool/page")
    public R poolPage(CustomerPageDTO dto) {
        return R.ok(customerService.poolPage(dto));
    }
}
