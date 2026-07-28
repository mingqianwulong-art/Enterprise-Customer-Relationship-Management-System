package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 * <p>
 * 注意：该表无 deleted 字段，不继承 BaseEntity，仅保留 create_time
 *
 * @author CRM
 */
@Data
@TableName("sys_log")
public class SysLog {

    /** 日志ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    @TableField("user_id")
    private Long userId;

    /** 用户名 */
    @TableField("username")
    private String username;

    /** 操作描述 */
    @TableField("operation")
    private String operation;

    /** 请求方法（类名.方法名） */
    @TableField("method")
    private String method;

    /** 请求URL */
    @TableField("request_url")
    private String requestUrl;

    /** 请求方式（GET/POST/PUT/DELETE） */
    @TableField("request_method")
    private String requestMethod;

    /** 请求参数 */
    @TableField("params")
    private String params;

    /** 返回结果 */
    @TableField("result")
    private String result;

    /** IP地址 */
    @TableField("ip")
    private String ip;

    /** 耗时（毫秒） */
    @TableField("cost_time")
    private Long costTime;

    /** 创建时间 */
    @TableField("create_time")
    private LocalDateTime createTime;
}
