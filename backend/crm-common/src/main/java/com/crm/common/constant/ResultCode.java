package com.crm.common.constant;

/**
 * 返回状态码枚举
 */
public enum ResultCode {

    /** 成功 */
    SUCCESS(200, "成功"),
    /** 未授权（未登录或 Token 失效） */
    UNAUTHORIZED(401, "未授权"),
    /** 禁止访问（权限不足） */
    FORBIDDEN(403, "禁止访问"),
    /** 资源不存在 */
    NOT_FOUND(404, "资源不存在"),
    /** 系统内部错误 */
    INTERNAL_ERROR(500, "系统内部错误"),
    /** 业务错误 */
    BUSINESS_ERROR(1001, "业务错误");

    /** 状态码 */
    private final int code;

    /** 提示信息 */
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
