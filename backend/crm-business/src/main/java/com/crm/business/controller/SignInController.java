package com.crm.business.controller;

import com.crm.business.entity.SignIn;
import com.crm.business.mapper.SignInMapper;
import com.crm.common.api.R;
import com.crm.common.constant.Perms;
import com.crm.common.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 外勤签到 Controller
 * <p>
 * 移动端外勤签到打卡功能
 *
 * @author CRM
 */
@Tag(name = "外勤签到")
@RestController
@RequestMapping("/business/sign-in")
public class SignInController {

    @Autowired
    private SignInMapper signInMapper;

    /**
     * 签到打卡
     */
    @Operation(summary = "签到打卡")
    @PreAuthorize("hasAuthority('" + Perms.SIGN_IN_ADD + "')")
    @PostMapping
    public R<SignIn> signIn(@RequestBody SignIn signIn) {
        Long userId = SecurityUtils.getCurrentUserIdRequired();
        signIn.setUserId(userId);
        signIn.setSignTime(LocalDateTime.now());
        signIn.setCreateTime(LocalDateTime.now());
        signInMapper.insert(signIn);
        return R.ok(signIn);
    }

    /**
     * 查询今日签到记录
     */
    @Operation(summary = "查询今日签到记录")
    @PreAuthorize("hasAuthority('" + Perms.SIGN_IN_LIST + "')")
    @GetMapping("/today")
    public R<List<SignIn>> getTodaySignIn() {
        Long userId = SecurityUtils.getCurrentUserIdRequired();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        LambdaQueryWrapper<SignIn> wrapper = new LambdaQueryWrapper<SignIn>()
                .eq(SignIn::getUserId, userId)
                .ge(SignIn::getSignTime, todayStart)
                .lt(SignIn::getSignTime, todayEnd)
                .orderByAsc(SignIn::getSignTime);
        return R.ok(signInMapper.selectList(wrapper));
    }

    /**
     * 查询签到记录（分页）
     */
    @Operation(summary = "查询签到记录")
    @PreAuthorize("hasAuthority('" + Perms.SIGN_IN_LIST + "')")
    @GetMapping
    public R<List<SignIn>> list(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Long userId = SecurityUtils.getCurrentUserIdRequired();
        LambdaQueryWrapper<SignIn> wrapper = new LambdaQueryWrapper<SignIn>()
                .eq(SignIn::getUserId, userId)
                .orderByDesc(SignIn::getSignTime);
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(SignIn::getSignTime, startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(SignIn::getSignTime, endDate + " 23:59:59");
        }
        return R.ok(signInMapper.selectList(wrapper));
    }
}
