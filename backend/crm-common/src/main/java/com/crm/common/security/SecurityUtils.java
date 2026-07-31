package com.crm.common.security;

import com.crm.common.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全上下文工具类
 * <p>
 * 从 SecurityContext 中获取当前登录用户信息
 *
 * @author CRM
 */
public class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * 获取当前登录用户
     *
     * @return LoginUser，若未登录或 principal 不是 LoginUser 则返回 null
     */
    public static LoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 获取当前登录用户ID
     *
     * @return userId，若未登录则返回 null
     */
    public static Long getCurrentUserId() {
        LoginUser loginUser = getCurrentUser();
        return loginUser == null ? null : loginUser.getUserId();
    }

    /**
     * 获取当前登录用户ID（必须已登录）
     *
     * @return userId
     * @throws BusinessException 未登录或登录已过期
     */
    public static Long getCurrentUserIdRequired() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("未登录或登录已过期");
        }
        return userId;
    }

    /**
     * 获取当前登录用户名
     *
     * @return username，若未登录则返回 null
     */
    public static String getCurrentUsername() {
        LoginUser loginUser = getCurrentUser();
        return loginUser == null ? null : loginUser.getUsername();
    }

    /**
     * 判断给定 userId 是否为当前登录用户
     *
     * @param userId 用户ID
     * @return true 表示是当前登录用户
     */
    public static boolean isCurrentUser(Long userId) {
        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }
}
