package com.crm.report.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 销售业绩排行 VO
 */
@Data
public class SalesRankingVO {

    /** 用户ID */
    private Long userId;

    /** 销售人员姓名 */
    private String realName;

    /** 负责客户数 */
    private Long customerCount;

    /** 商机数 */
    private Long opportunityCount;

    /** 赢单数 */
    private Long wonCount;

    /** 合同总金额 */
    private BigDecimal contractAmount;

    /** 回款总金额 */
    private BigDecimal receivedAmount;
}
