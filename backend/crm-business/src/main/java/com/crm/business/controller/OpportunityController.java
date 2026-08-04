package com.crm.business.controller;

import com.crm.business.entity.Opportunity;
import com.crm.business.service.IOpportunityService;
import com.crm.business.vo.OpportunityPageDTO;
import com.crm.common.api.R;
import com.crm.common.constant.Perms;
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
 * 商机控制器
 *
 * @author CRM
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "商机管理")
@RestController
@RequestMapping("/business/opportunity")
public class OpportunityController {

    @Autowired
    private IOpportunityService opportunityService;

    /**
     * 分页查询商机
     */
    @Operation(summary = "分页查询商机")
    @PreAuthorize("hasAuthority('" + Perms.OPPORTUNITY_LIST + "')")
    @GetMapping("/page")
    public R page(OpportunityPageDTO dto) {
        return R.ok(opportunityService.page(dto));
    }

    /**
     * 商机详情
     */
    @Operation(summary = "商机详情")
    @PreAuthorize("hasAuthority('" + Perms.OPPORTUNITY_LIST + "')")
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        return R.ok(opportunityService.getById(id));
    }

    /**
     * 新增商机
     */
    @Operation(summary = "新增商机")
    @Log("新增商机")
    @PreAuthorize("hasAuthority('" + Perms.OPPORTUNITY_ADD + "')")
    @PostMapping
    public R add(@RequestBody Opportunity opportunity) {
        return opportunityService.addOpportunity(opportunity) ? R.ok("新增商机成功") : R.fail("新增商机失败");
    }

    /**
     * 修改商机
     */
    @Operation(summary = "修改商机")
    @PreAuthorize("hasAuthority('" + Perms.OPPORTUNITY_EDIT + "')")
    @PutMapping
    public R update(@RequestBody Opportunity opportunity) {
        return opportunityService.updateOpportunity(opportunity) ? R.ok("修改商机成功") : R.fail("修改商机失败");
    }

    /**
     * 删除商机
     */
    @Operation(summary = "删除商机")
    @Log("删除商机")
    @PreAuthorize("hasAuthority('" + Perms.OPPORTUNITY_DELETE + "')")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return opportunityService.deleteOpportunity(id) ? R.ok("删除商机成功") : R.fail("删除商机失败");
    }

    /**
     * 修改商机阶段
     */
    @Operation(summary = "修改商机阶段")
    @PreAuthorize("hasAuthority('" + Perms.OPPORTUNITY_STAGE + "')")
    @PutMapping("/{id}/stage")
    public R changeStage(@PathVariable Long id, @RequestParam Integer stage) {
        return opportunityService.changeStage(id, stage) ? R.ok("修改阶段成功") : R.fail("修改阶段失败");
    }

    /**
     * 销售漏斗数据
     */
    @Operation(summary = "销售漏斗数据")
    @PreAuthorize("hasAuthority('" + Perms.OPPORTUNITY_LIST + "')")
    @GetMapping("/funnel")
    public R funnel() {
        return R.ok(opportunityService.getFunnelData());
    }

    /**
     * 停滞预警商机列表（超过指定天数未推进阶段的进行中商机）
     */
    @Operation(summary = "停滞预警商机列表")
    @PreAuthorize("hasAuthority('" + Perms.OPPORTUNITY_LIST + "')")
    @GetMapping("/stagnant")
    public R stagnant(@RequestParam(defaultValue = "15") Integer days) {
        return R.ok(opportunityService.listStagnant(days));
    }
}
