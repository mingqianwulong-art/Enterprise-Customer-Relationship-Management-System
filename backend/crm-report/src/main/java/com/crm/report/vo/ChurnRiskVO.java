package com.crm.report.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 高流失风险客户 VO
 * <p>
 * 识别长时间未跟进、有流失风险的客户
 *
 * @author CRM
 */
@Data
public class ChurnRiskVO {

    /** 客户ID */
    private Long customerId;

    /** 客户名称 */
    private String customerName;

    /** 客户等级（1普通 2重要 3VIP） */
    private Integer customerLevel;

    /** 负责人ID */
    private Long ownerId;

    /** 负责人姓名 */
    private String ownerName;

    /** 最后跟进时间 */
    private LocalDateTime lastFollowTime;

    /** 距今天数（未跟进天数） */
    private Integer daysSinceLastFollow;

    /** 累计成交金额 */
    private BigDecimal totalAmount;

    /** 流失风险等级（1低 2中 3高） */
    private Integer riskLevel;

    /** 风险等级描述 */
    private String riskLevelText;

    /** 建议挽留动作 */
    private String suggestedAction;
}
