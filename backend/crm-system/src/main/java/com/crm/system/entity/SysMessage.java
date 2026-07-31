package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统消息实体类
 *
 * @author CRM
 */
@Data
@TableName("sys_message")
public class SysMessage {

    /** 消息ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 接收人ID */
    @TableField("user_id")
    private Long userId;

    /** 消息标题 */
    @TableField("title")
    private String title;

    /** 消息内容 */
    @TableField("content")
    private String content;

    /** 消息类型 1跟进提醒 2商机预警 3系统通知 */
    @TableField("type")
    private Integer type;

    /** 关联业务ID */
    @TableField("ref_id")
    private Long refId;

    /** 关联业务类型 */
    @TableField("ref_type")
    private String refType;

    /** 是否已读 0未读 1已读 */
    @TableField("is_read")
    private Integer isRead;

    /** 创建时间 */
    @TableField("create_time")
    private LocalDateTime createTime;
}
