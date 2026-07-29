package com.crm.market.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 渠道效果统计VO
 * <p>
 * 用于渠道效果分析看板，统计每个渠道的线索数和转化率
 *
 * @author CRM
 */
@Data
public class ChannelStatsVO {

    /** 渠道ID */
    private Long channelId;

    /** 渠道名称 */
    private String channelName;

    /** 渠道类型 */
    private String channelType;

    /** 线索数 */
    private Integer clueCount;

    /** 投放成本 */
    private BigDecimal cost;

    /** 转化率 */
    private BigDecimal conversionRate;
}
