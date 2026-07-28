package com.crm.system.aspect;

import com.crm.system.annotation.Log;
import com.crm.system.entity.SysLog;
import com.crm.system.service.ISysLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 操作日志切面
 * <p>
 * 环绕通知，拦截带有 @Log 注解的方法，记录操作日志到 sys_log 表
 *
 * @author CRM
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    @Autowired
    private ISysLogService logService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 切点：标注了 @Log 注解的方法
     */
    @Pointcut("@annotation(logAnnotation)")
    public void logPointcut(Log logAnnotation) {
    }

    /**
     * 环绕通知：记录操作日志
     */
    @Around("logPointcut(logAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint, Log logAnnotation) throws Throwable {
        long startTime = System.currentTimeMillis();
        SysLog sysLog = new SysLog();
        // 操作描述
        sysLog.setOperation(logAnnotation.value());
        // 请求方法（类名.方法名）
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        sysLog.setMethod(className + "." + methodName + "()");
        // 请求参数
        try {
            sysLog.setParams(objectMapper.writeValueAsString(joinPoint.getArgs()));
        } catch (Exception e) {
            sysLog.setParams(Arrays.toString(joinPoint.getArgs()));
        }
        // 请求URL和方式
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            sysLog.setRequestUrl(request.getRequestURI());
            sysLog.setRequestMethod(request.getMethod());
            sysLog.setIp(getIpAddress(request));
        }
        // 当前登录用户
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Long) {
                sysLog.setUserId((Long) authentication.getPrincipal());
            }
        } catch (Exception e) {
            log.warn("获取当前用户信息失败：{}", e.getMessage());
        }

        Object result = null;
        Exception exception = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            sysLog.setResult("success");
        } catch (Exception e) {
            exception = e;
            sysLog.setResult("error: " + e.getMessage());
            throw e;
        } finally {
            // 耗时
            long costTime = System.currentTimeMillis() - startTime;
            sysLog.setCostTime(costTime);
            sysLog.setCreateTime(LocalDateTime.now());
            // 异步保存日志
            try {
                logService.asyncSaveLog(sysLog);
            } catch (Exception e) {
                log.error("保存操作日志失败：{}", e.getMessage(), e);
            }
        }
        return result;
    }

    /**
     * 获取客户端 IP 地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
