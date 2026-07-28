package com.crm.customer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户实体类
 *
 * @author CRM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cus_customer")
public class Customer extends BaseEntity {

    /** 客户ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 客户名称 */
    @TableField("name")
    private String name;

    /** 统一社会信用代码（唯一） */
    @TableField("credit_code")
    private String creditCode;

    /** 所属行业 */
    @TableField("industry")
    private String industry;

    /** 所在区域 */
    @TableField("region")
    private String region;

    /** 客户等级（1普通 2重要 3VIP） */
    @TableField("customer_level")
    private Integer customerLevel;

    /** 负责人ID */
    @TableField("owner_id")
    private Long ownerId;

    /** 是否公海客户（0否 1是） */
    @TableField("in_pool")
    private Integer inPool;

    /** 最后跟进时间 */
    @TableField("last_follow_time")
    private LocalDateTime lastFollowTime;

    /** 累计成交金额 */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /** 备注 */
    @TableField("remark")
    private String remark;
}
