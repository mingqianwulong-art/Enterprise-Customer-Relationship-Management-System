package com.crm.market.vo;

import lombok.Data;

/**
 * 线索分页查询条件
 *
 * @author CRM
 */
@Data
public class CluePageDTO {

    /** 当前页 */
    private Integer pageNum = 1;

    /** 每页条数 */
    private Integer pageSize = 10;

    /** 线索名称（模糊查询） */
    private String clueName;

    /** 线索来源 */
    private String source;

    /** 所属行业 */
    private String industry;

    /** 所在区域 */
    private String region;

    /** 状态（0待分配 1已分配 2已转化 3已废弃） */
    private Integer status;

    /** 线索等级（1低 2中 3高） */
    private Integer level;

    /** 渠道ID */
    private Long channelId;
}
