package com.crm.market.controller;

import com.crm.common.api.R;
import com.crm.common.constant.Perms;
import com.crm.common.security.SecurityUtils;
import com.crm.market.entity.Clue;
import com.crm.market.service.ClueAssignRuleService;
import com.crm.market.service.IClueService;
import com.crm.market.vo.CluePageDTO;
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
 * 线索控制器
 *
 * @author CRM
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "线索管理")
@RestController
@RequestMapping("/market/clue")
public class ClueController {

    @Autowired
    private IClueService clueService;

    @Autowired
    private ClueAssignRuleService clueAssignRuleService;

    /**
     * 分页查询线索
     */
    @Operation(summary = "分页查询线索")
    @PreAuthorize("hasAuthority('" + Perms.CLUE_LIST + "')")
    @GetMapping("/page")
    public R page(CluePageDTO dto) {
        return R.ok(clueService.page(dto));
    }

    /**
     * 线索详情
     */
    @Operation(summary = "线索详情")
    @PreAuthorize("hasAuthority('" + Perms.CLUE_LIST + "')")
    @GetMapping("/{id}")
    public R getDetail(@PathVariable Long id) {
        return R.ok(clueService.getById(id));
    }

    /**
     * 新增线索
     */
    @Operation(summary = "新增线索")
    @Log("新增线索")
    @PreAuthorize("hasAuthority('" + Perms.CLUE_ADD + "')")
    @PostMapping
    public R add(@RequestBody Clue clue) {
        return clueService.addClue(clue) ? R.ok("新增线索成功") : R.fail("新增线索失败");
    }

    /**
     * 修改线索
     */
    @Operation(summary = "修改线索")
    @PreAuthorize("hasAuthority('" + Perms.CLUE_EDIT + "')")
    @PutMapping
    public R update(@RequestBody Clue clue) {
        return clueService.updateClue(clue) ? R.ok("修改线索成功") : R.fail("修改线索失败");
    }

    /**
     * 删除线索
     */
    @Operation(summary = "删除线索")
    @PreAuthorize("hasAuthority('" + Perms.CLUE_DELETE + "')")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return clueService.deleteClue(id) ? R.ok("删除线索成功") : R.fail("删除线索失败");
    }

    /**
     * 分配线索
     */
    @Operation(summary = "分配线索")
    @PreAuthorize("hasAuthority('" + Perms.CLUE_ASSIGN + "')")
    @PutMapping("/{id}/assign")
    public R assign(@PathVariable Long id, @RequestParam Long userId) {
        return clueService.assignClue(id, userId) ? R.ok("分配线索成功") : R.fail("分配线索失败");
    }

    /**
     * 抢单
     */
    @Operation(summary = "抢单")
    @PreAuthorize("hasAuthority('" + Perms.CLUE_CLAIM + "')")
    @PutMapping("/{id}/claim")
    public R claim(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserIdRequired();
        return clueService.claimClue(id, userId) ? R.ok("抢单成功") : R.fail("抢单失败");
    }

    /**
     * 转化为客户
     */
    @Operation(summary = "转化为客户")
    @PreAuthorize("hasAuthority('" + Perms.CLUE_CONVERT + "')")
    @PutMapping("/{id}/convert")
    public R convert(@PathVariable Long id, @RequestParam Long customerId) {
        return clueService.convertClue(id, customerId) ? R.ok("转化成功") : R.fail("转化失败");
    }

    /**
     * 自动分配线索（手动触发规则引擎）
     */
    @Operation(summary = "自动分配线索")
    @PreAuthorize("hasAuthority('" + Perms.CLUE_AUTO_ASSIGN + "')")
    @PutMapping("/{id}/auto-assign")
    public R autoAssign(@PathVariable Long id) {
        Long userId = clueAssignRuleService.autoAssign(id);
        return userId != null ? R.ok("自动分配成功，分配销售ID：" + userId) : R.fail("无可用销售或线索已分配");
    }

    /**
     * 批量自动分配所有待分配线索
     */
    @Operation(summary = "批量自动分配待分配线索")
    @PreAuthorize("hasAuthority('" + Perms.CLUE_AUTO_ASSIGN + "')")
    @PostMapping("/auto-assign-all")
    public R autoAssignAll() {
        int count = clueAssignRuleService.batchAutoAssign();
        return R.ok("批量自动分配完成，成功分配 " + count + " 条线索");
    }
}
