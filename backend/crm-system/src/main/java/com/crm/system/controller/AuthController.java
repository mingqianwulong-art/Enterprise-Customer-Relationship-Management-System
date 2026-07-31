package com.crm.system.controller;

import com.crm.common.api.R;
import com.crm.common.constant.Constants;
import com.crm.common.security.SecurityUtils;
import com.crm.system.annotation.Log;
import com.crm.system.dto.ForgotPasswordDTO;
import com.crm.system.dto.LoginDTO;
import com.crm.system.dto.RegisterDTO;
import com.crm.system.dto.SmsCodeDTO;
import com.crm.system.service.ISysUserService;
import com.crm.system.service.SmsService;
import com.crm.system.vo.LoginUserVO;
import com.crm.system.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 *
 * @author CRM
 */
@Tag(name = "登录认证")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SmsService smsService;

    /**
     * 登录
     */
    @Operation(summary = "用户登录")
    @Log("用户登录")
    @PostMapping("/login")
    public R login(@Parameter(description = "登录信息") @RequestBody @Valid LoginDTO dto) {
        LoginVO vo = userService.login(dto.getUsername(), dto.getPassword());
        return R.ok(vo);
    }

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public R register(@RequestBody @Valid RegisterDTO dto) {
        userService.register(dto);
        return R.ok("注册成功");
    }

    /**
     * 发送短信验证码
     * 开发模式（sms.enabled=false）：验证码在控制台日志打印
     * 生产模式（sms.enabled=true）：真实发送到手机
     */
    @Operation(summary = "发送短信验证码")
    @PostMapping("/sms/send")
    public R sendSmsCode(@RequestBody @Valid SmsCodeDTO dto) {
        smsService.sendCode(dto.getPhone());
        return R.ok("验证码已发送，请查收短信");
    }

    /**
     * 忘记密码（通过手机号验证码重置）
     */
    @Operation(summary = "忘记密码")
    @PostMapping("/forgot-password")
    public R forgotPassword(@RequestBody @Valid ForgotPasswordDTO dto) {
        userService.forgotPassword(dto);
        return R.ok("密码重置成功，请使用新密码登录");
    }

    /**
     * 获取当前登录用户信息
     */
    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/info")
    public R info() {
        Long userId = getCurrentUserId();
        LoginUserVO vo = userService.getInfo(userId);
        return R.ok(vo);
    }

    /**
     * 退出登录
     */
    @Operation(summary = "退出登录")
    @Log("退出登录")
    @PostMapping("/logout")
    public R logout() {
        Long userId = getCurrentUserId();
        // 删除 Redis 中的登录令牌
        redisTemplate.delete(Constants.LOGIN_TOKEN_KEY + userId);
        return R.ok();
    }

    /**
     * 从 SecurityContext 获取当前用户ID
     */
    private Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserIdRequired();
    }
}
