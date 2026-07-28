package com.crm.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.api.R;
import com.crm.system.entity.SysLog;
import com.crm.system.service.ISysLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志控制器
 *
 * @author CRM
 */
@Tag(name = "操作日志")
@RestController
@RequestMapping("/system/log")
public class SysLogController {

    @Autowired
    private ISysLogService logService;

    /**
     * 分页查询操作日志
     */
    @Operation(summary = "分页查询操作日志")
    @GetMapping("/page")
    public R page(@RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize,
                  @RequestParam(required = false) String username,
                  @RequestParam(required = false) String operation) {
        Page<SysLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<SysLog>()
                .like(username != null && !username.isEmpty(), SysLog::getUsername, username)
                .like(operation != null && !operation.isEmpty(), SysLog::getOperation, operation)
                .orderByDesc(SysLog::getCreateTime);
        return R.ok(logService.page(page, wrapper));
    }
}
