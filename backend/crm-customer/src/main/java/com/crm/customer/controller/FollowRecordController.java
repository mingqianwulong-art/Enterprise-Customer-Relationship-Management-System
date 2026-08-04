package com.crm.customer.controller;

import com.crm.common.api.R;
import com.crm.common.constant.Perms;
import com.crm.common.security.SecurityUtils;
import com.crm.customer.entity.FollowRecord;
import com.crm.customer.service.IFollowRecordService;
import com.crm.system.annotation.Log;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 跟进记录控制器
 *
 * @author CRM
 */
@Tag(name = "跟进记录")
@RestController
@RequestMapping("/customer/follow")
public class FollowRecordController {

    @Autowired
    private IFollowRecordService followRecordService;

    /**
     * 查某客户的跟进记录
     */
    @Operation(summary = "查询客户跟进记录")
    @PreAuthorize("hasAuthority('" + Perms.CUSTOMER_FOLLOW_LIST + "')")
    @GetMapping("/list/{customerId}")
    public R listByCustomerId(@PathVariable Long customerId) {
        return R.ok(followRecordService.listByCustomerId(customerId));
    }

    /**
     * 今日待跟进列表
     */
    @Operation(summary = "今日待跟进列表")
    @PreAuthorize("hasAuthority('" + Perms.CUSTOMER_FOLLOW_LIST + "')")
    @GetMapping("/today")
    public R todayPending() {
        return R.ok(followRecordService.listTodayPending());
    }

    /**
     * 新增跟进记录（自动更新客户最后跟进时间）
     */
    @Operation(summary = "新增跟进记录")
    @Log("新增跟进")
    @PreAuthorize("hasAuthority('" + Perms.CUSTOMER_FOLLOW_ADD + "')")
    @PostMapping
    public R add(@RequestBody FollowRecord record) {
        // 覆写 userId 为当前登录用户，防止前端伪造
        record.setUserId(SecurityUtils.getCurrentUserIdRequired());
        return followRecordService.addFollowRecord(record) ? R.ok("新增跟进记录成功") : R.fail("新增跟进记录失败");
    }
}
