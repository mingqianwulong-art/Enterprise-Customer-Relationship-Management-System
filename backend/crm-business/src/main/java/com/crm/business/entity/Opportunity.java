package com.crm.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商机实体类
 *
 * @author CRM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bus_opportunity")
public class Opportunity extends BaseEntity {

    /** 商机ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商机名称 */
    @TableField("opp_name")
    private String oppName;

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 客户名称 */
    @TableField("customer_name")
    private String customerName;

    /** 联系人ID */
    @TableField("contact_id")
    private Long contactId;

    /** 预计成交金额 */
    @TableField("estimated_amount")
    private BigDecimal estimatedAmount;

    /** 商机阶段（1需求确认 2方案报价 3商务谈判 4合同签订 5已赢单 6已输单） */
    @TableField("stage")
    private Integer stage;

    /** 阶段最后变更时间（用于停滞预警检测） */
    @TableField("stage_change_time")
    private LocalDateTime stageChangeTime;

    /** 成交概率 */
    @TableField("probability")
    private Integer probability;

    /** 预计成交日期 */
    @TableField("expected_close_date")
    private LocalDate expectedCloseDate;

    /** 负责人ID */
    @TableField("owner_id")
    private Long ownerId;

    /** 商机来源 */
    @TableField("source")
    private String source;

    /** 商机描述 */
    @TableField("description")
    private String description;
}
