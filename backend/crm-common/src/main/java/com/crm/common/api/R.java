package com.crm.common.api;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 *
 * @param <T> 数据类型
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 成功状态码 */
    public static final int SUCCESS_CODE = 200;

    /** 失败状态码 */
    public static final int FAIL_CODE = 500;

    /** 状态码 */
    private int code;

    /** 提示信息 */
    private String msg;

    /** 数据 */
    private T data;

    public R() {
    }

    public R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功返回（无数据）
     *
     * @param <T> 数据类型
     * @return 统一返回结果
     */
    public static <T> R<T> ok() {
        return new R<>(SUCCESS_CODE, "操作成功", null);
    }

    /**
     * 成功返回（带数据）
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 统一返回结果
     */
    public static <T> R<T> ok(T data) {
        return new R<>(SUCCESS_CODE, "操作成功", data);
    }

    /**
     * 成功返回（带提示信息和数据）
     *
     * @param msg  提示信息
     * @param data 数据
     * @param <T>  数据类型
     * @return 统一返回结果
     */
    public static <T> R<T> ok(String msg, T data) {
        return new R<>(SUCCESS_CODE, msg, data);
    }

    /**
     * @deprecated 请使用 ok() 替代
     */
    public static <T> R<T> success() {
        return ok();
    }

    /**
     * @deprecated 请使用 ok(T data) 替代
     */
    public static <T> R<T> success(T data) {
        return ok(data);
    }

    /**
     * @deprecated 请使用 ok(String msg, T data) 替代
     */
    public static <T> R<T> success(String msg, T data) {
        return ok(msg, data);
    }

    /**
     * 失败返回（默认提示）
     *
     * @param <T> 数据类型
     * @return 统一返回结果
     */
    public static <T> R<T> fail() {
        return new R<>(FAIL_CODE, "操作失败", null);
    }

    /**
     * 失败返回（带提示信息）
     *
     * @param msg 提示信息
     * @param <T> 数据类型
     * @return 统一返回结果
     */
    public static <T> R<T> fail(String msg) {
        return new R<>(FAIL_CODE, msg, null);
    }

    /**
     * 失败返回（带状态码和提示信息）
     *
     * @param code 状态码
     * @param msg  提示信息
     * @param <T>  数据类型
     * @return 统一返回结果
     */
    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, msg, null);
    }

    /**
     * @deprecated 请使用 fail() 替代
     */
    public static <T> R<T> failed() {
        return fail();
    }

    /**
     * @deprecated 请使用 fail(String msg) 替代
     */
    public static <T> R<T> failed(String msg) {
        return fail(msg);
    }

    /**
     * @deprecated 请使用 fail(int code, String msg) 替代
     */
    public static <T> R<T> failed(int code, String msg) {
        return fail(code, msg);
    }
}
