package com.crm.common.constant;

/**
 * 系统常量
 * <p>
 * 定义 JWT 相关常量与 Redis key 前缀
 */
public class Constants {

    private Constants() {
    }

    /** JWT Token 前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** JWT Token 请求头名称 */
    public static final String TOKEN_HEADER = "Authorization";

    /** JWT Token 过期时间（分钟） */
    public static final int TOKEN_EXPIRE = 1440;

    /** 登录 Token Redis key 前缀 */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /** 验证码 Redis key 前缀 */
    public static final String CAPTCHA_KEY = "captcha:";
}
