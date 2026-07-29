package com.crm.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 渠道实体类
 *
 * @author CRM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("market_channel")
public class Channel extends BaseEntity {

    /** 渠道ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 渠道名称 */
    private String channelName;

    /** 渠道类型 */
    private String channelType;

    /** 联系人 */
    private String contact;

    /** 联系电话 */
    private String phone;

    /** 投放成本 */
    private BigDecimal cost;

    /** 状态（0禁用 1启用） */
    private Integer status;

    /** 备注 */
    private String remark;
}
