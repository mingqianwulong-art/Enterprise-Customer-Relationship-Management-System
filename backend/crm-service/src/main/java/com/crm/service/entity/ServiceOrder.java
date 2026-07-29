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
 * 工单实体类
 *
 * @author CRM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ser_order")
public class ServiceOrder extends BaseEntity {

    /** 工单ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 工单编号 */
    @TableField("order_no")
    private String orderNo;

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 客户名称 */
    @TableField("customer_name")
    private String customerName;

    /** 联系人ID */
    @TableField("contact_id")
    private Long contactId;

    /** 联系人姓名 */
    @TableField("contact_name")
    private String contactName;

    /** 工单标题 */
    @TableField("title")
    private String title;

    /** 问题描述 */
    @TableField("description")
    private String description;

    /** 工单类型 */
    @TableField("type")
    private Integer type;

    /** 来源 */
    @TableField("source")
    private Integer source;

    /** 优先级 */
    @TableField("priority")
    private Integer priority;

    /** 状态 */
    @TableField("status")
    private Integer status;

    /** 处理人ID */
    @TableField("assignee_id")
    private Long assigneeId;

    /** 处理人姓名 */
    @TableField("assignee_name")
    private String assigneeName;

    /** 解决时间 */
    @TableField("resolve_time")
    private LocalDateTime resolveTime;

    /** 满意度评分 */
    @TableField("satisfaction")
    private Integer satisfaction;

    /** 满意度评价 */
    @TableField("satisfaction_comment")
    private String satisfactionComment;
}
