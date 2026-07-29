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

/**
 * 回款实体类
 *
 * @author CRM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bus_payment")
public class Payment extends BaseEntity {

    /** 回款ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 合同ID */
    @TableField("contract_id")
    private Long contractId;

    /** 合同编号 */
    @TableField("contract_no")
    private String contractNo;

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 客户名称 */
    @TableField("customer_name")
    private String customerName;

    /** 回款编号 */
    @TableField("payment_no")
    private String paymentNo;

    /** 计划回款日期 */
    @TableField("plan_date")
    private LocalDate planDate;

    /** 实际回款日期 */
    @TableField("actual_date")
    private LocalDate actualDate;

    /** 计划回款金额 */
    @TableField("plan_amount")
    private BigDecimal planAmount;

    /** 实际回款金额 */
    @TableField("actual_amount")
    private BigDecimal actualAmount;

    /** 回款阶段 */
    @TableField("payment_stage")
    private String paymentStage;

    /** 状态（0待回款 1部分回款 2已回款 3已逾期） */
    @TableField("status")
    private Integer status;

    /** 负责人ID */
    @TableField("owner_id")
    private Long ownerId;

    /** 备注 */
    @TableField("remark")
    private String remark;
}
