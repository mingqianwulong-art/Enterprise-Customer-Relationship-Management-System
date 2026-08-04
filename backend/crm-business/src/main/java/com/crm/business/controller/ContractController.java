package com.crm.business.controller;

import com.crm.business.entity.Contract;
import com.crm.business.service.IContractService;
import com.crm.business.vo.ContractPageDTO;
import com.crm.common.api.R;
import com.crm.common.constant.Perms;
import com.crm.common.security.SecurityUtils;
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
 * 合同控制器
 *
 * @author CRM
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "合同管理")
@RestController
@RequestMapping("/business/contract")
public class ContractController {

    @Autowired
    private IContractService contractService;

    /**
     * 分页查询合同
     */
    @Operation(summary = "分页查询合同")
    @PreAuthorize("hasAuthority('" + Perms.CONTRACT_LIST + "')")
    @GetMapping("/page")
    public R page(ContractPageDTO dto) {
        return R.ok(contractService.page(dto));
    }

    /**
     * 合同详情
     */
    @Operation(summary = "合同详情")
    @PreAuthorize("hasAuthority('" + Perms.CONTRACT_LIST + "')")
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        return R.ok(contractService.getById(id));
    }

    /**
     * 新增合同
     */
    @Operation(summary = "新增合同")
    @Log("新增合同")
    @PreAuthorize("hasAuthority('" + Perms.CONTRACT_ADD + "')")
    @PostMapping
    public R add(@RequestBody Contract contract) {
        return contractService.addContract(contract) ? R.ok("新增合同成功") : R.fail("新增合同失败");
    }

    /**
     * 修改合同
     */
    @Operation(summary = "修改合同")
    @PreAuthorize("hasAuthority('" + Perms.CONTRACT_EDIT + "')")
    @PutMapping
    public R update(@RequestBody Contract contract) {
        return contractService.updateContract(contract) ? R.ok("修改合同成功") : R.fail("修改合同失败");
    }

    /**
     * 删除合同
     */
    @Operation(summary = "删除合同")
    @Log("删除合同")
    @PreAuthorize("hasAuthority('" + Perms.CONTRACT_DELETE + "')")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return contractService.deleteContract(id) ? R.ok("删除合同成功") : R.fail("删除合同失败");
    }

    /**
     * 审批合同（审批人从当前登录用户获取，前端无需传 approverId）
     */
    @Operation(summary = "审批合同")
    @Log("审批合同")
    @PreAuthorize("hasAuthority('" + Perms.CONTRACT_APPROVE + "')")
    @PutMapping("/{id}/approve")
    public R approve(@PathVariable Long id) {
        Long approverId = SecurityUtils.getCurrentUserIdRequired();
        return contractService.approve(id, approverId) ? R.ok("审批合同成功") : R.fail("审批合同失败");
    }
}
