package com.crm.common.exception;

/**
 * 业务异常
 * <p>
 * 用于在业务逻辑中抛出可控的异常，由全局异常处理器捕获并返回统一结果
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 状态码 */
    private final int code;

    /** 提示信息 */
    private final String msg;

    /**
     * 构造方法（默认状态码 500）
     *
     * @param msg 提示信息
     */
    public BusinessException(String msg) {
        super(msg);
        this.code = 500;
        this.msg = msg;
    }

    /**
     * 构造方法
     *
     * @param code 状态码
     * @param msg  提示信息
     */
    public BusinessException(int code, String msg) {
        super(msg);
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
