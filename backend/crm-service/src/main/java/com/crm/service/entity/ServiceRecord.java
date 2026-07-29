package com.crm.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 售后记录实体类
 *
 * @author CRM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ser_record")
public class ServiceRecord extends BaseEntity {

    /** 售后记录ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联工单ID */
    @TableField("order_id")
    private Long orderId;

    /** 关联工单编号 */
    @TableField("order_no")
    private String orderNo;

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 客户名称 */
    @TableField("customer_name")
    private String customerName;

    /** 记录类型 */
    @TableField("type")
    private Integer type;

    /** 记录标题 */
    @TableField("title")
    private String title;

    /** 记录内容 */
    @TableField("content")
    private String content;

    /** 处理结果 */
    @TableField("result")
    private String result;

    /** 处理人ID */
    @TableField("handler_id")
    private Long handlerId;

    /** 处理人姓名 */
    @TableField("handler_name")
    private String handlerName;

    /** 处理时间 */
    @TableField("handle_time")
    private LocalDateTime handleTime;

    /** 附件URL */
    @TableField("attachments")
    private String attachments;
}
