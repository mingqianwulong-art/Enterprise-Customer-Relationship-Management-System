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
 * 合同实体类
 *
 * @author CRM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bus_contract")
public class Contract extends BaseEntity {

    /** 合同ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 合同编号 */
    @TableField("contract_no")
    private String contractNo;

    /** 合同名称 */
    @TableField("contract_name")
    private String contractName;

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 客户名称 */
    @TableField("customer_name")
    private String customerName;

    /** 关联商机ID */
    @TableField("opp_id")
    private Long oppId;

    /** 合同金额 */
    @TableField("amount")
    private BigDecimal amount;

    /** 签订日期 */
    @TableField("signed_date")
    private LocalDate signedDate;

    /** 开始日期 */
    @TableField("start_date")
    private LocalDate startDate;

    /** 结束日期 */
    @TableField("end_date")
    private LocalDate endDate;

    /** 状态（0待审批 1已审批 2已签订 3已作废） */
    @TableField("status")
    private Integer status;

    /** 审批人ID */
    @TableField("approver_id")
    private Long approverId;

    /** 审批时间 */
    @TableField("approve_time")
    private LocalDateTime approveTime;

    /** 负责人ID */
    @TableField("owner_id")
    private Long ownerId;

    /** 备注 */
    @TableField("remark")
    private String remark;
}
