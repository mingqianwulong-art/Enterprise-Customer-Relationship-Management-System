package com.crm.customer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 跟进记录实体类
 *
 * @author CRM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cus_follow_record")
public class FollowRecord extends BaseEntity {

    /** 跟进记录ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 跟进人ID */
    @TableField("user_id")
    private Long userId;

    /** 跟进方式（1电话 2拜访 3微信 4其他） */
    @TableField("follow_type")
    private Integer followType;

    /** 跟进内容 */
    @TableField("content")
    private String content;

    /** 下次跟进提醒时间 */
    @TableField("next_follow_time")
    private LocalDateTime nextFollowTime;

    /** 附件（多个附件路径逗号分隔） */
    @TableField("attachments")
    private String attachments;
}
