package com.crm.system.controller;

import com.crm.common.api.R;
import com.crm.common.security.SecurityUtils;
import com.crm.system.service.ISysMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统消息控制器
 *
 * @author CRM
 */
@Tag(name = "消息管理")
@RestController
@RequestMapping("/system/message")
public class SysMessageController {

    @Autowired
    private ISysMessageService messageService;

    /**
     * 未读消息列表
     */
    @Operation(summary = "未读消息列表")
    @GetMapping("/unread")
    public R unread() {
        return R.ok(messageService.listUnread(SecurityUtils.getCurrentUserIdRequired()));
    }

    /**
     * 所有消息列表
     */
    @Operation(summary = "所有消息列表")
    @GetMapping("/list")
    public R list() {
        return R.ok(messageService.listByUserId(SecurityUtils.getCurrentUserIdRequired()));
    }

    /**
     * 未读消息数量
     */
    @Operation(summary = "未读消息数量")
    @GetMapping("/unread-count")
    public R unreadCount() {
        return R.ok(messageService.countUnread(SecurityUtils.getCurrentUserIdRequired()));
    }

    /**
     * 标记消息已读
     */
    @Operation(summary = "标记消息已读")
    @PutMapping("/{id}/read")
    public R markAsRead(@PathVariable Long id) {
        return messageService.markAsRead(id) ? R.ok("已标记已读") : R.fail("操作失败");
    }

    /**
     * 标记全部已读
     */
    @Operation(summary = "标记全部已读")
    @PutMapping("/read-all")
    public R markAllAsRead() {
        int count = messageService.markAllAsRead(SecurityUtils.getCurrentUserIdRequired());
        return R.ok("已标记" + count + "条消息为已读");
    }
}
