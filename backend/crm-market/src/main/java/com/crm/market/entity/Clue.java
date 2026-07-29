package com.crm.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("market_clue")
public class Clue extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String clueName;      // 线索名称
    private String company;       // 公司名称
    private String phone;         // 联系电话
    private String email;          // 邮箱
    private String source;         // 线索来源
    private Long channelId;        // 渠道ID
    private String industry;       // 所属行业
    private String region;         // 所在区域
    private Integer level;         // 线索等级（1低 2中 3高）
    private Integer status;        // 状态（0待分配 1已分配 2已转化 3已废弃）
    private Long ownerId;          // 负责人ID
    private Long customerId;       // 转化后的客户ID
    private String description;    // 需求描述
}
