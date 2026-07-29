package com.crm.market.controller;

import com.crm.common.api.R;
import com.crm.market.entity.Channel;
import com.crm.market.service.IChannelService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 渠道控制器
 *
 * @author CRM
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "渠道管理")
@RestController
@RequestMapping("/market/channel")
public class ChannelController {

    @Autowired
    private IChannelService channelService;

    /**
     * 分页查询渠道
     */
    @Operation(summary = "分页查询渠道")
    @GetMapping("/page")
    public R page(@RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize,
                  @RequestParam(required = false) String channelName) {
        return R.ok(channelService.page(pageNum, pageSize, channelName));
    }

    /**
     * 查询所有启用渠道
     */
    @Operation(summary = "查询所有启用渠道")
    @GetMapping("/list")
    public R list() {
        return R.ok(channelService.list());
    }

    /**
     * 渠道效果统计
     */
    @Operation(summary = "渠道效果统计")
    @GetMapping("/stats")
    public R stats() {
        return R.ok(channelService.getStats());
    }

    /**
     * 新增渠道
     */
    @Operation(summary = "新增渠道")
    @PostMapping
    public R add(@RequestBody Channel channel) {
        return channelService.addChannel(channel) ? R.ok("新增渠道成功") : R.fail("新增渠道失败");
    }

    /**
     * 修改渠道
     */
    @Operation(summary = "修改渠道")
    @PutMapping
    public R update(@RequestBody Channel channel) {
        return channelService.updateChannel(channel) ? R.ok("修改渠道成功") : R.fail("修改渠道失败");
    }

    /**
     * 删除渠道
     */
    @Operation(summary = "删除渠道")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return channelService.deleteChannel(id) ? R.ok("删除渠道成功") : R.fail("删除渠道失败");
    }
}
