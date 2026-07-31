package com.crm.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 外勤签到实体类
 *
 * @author CRM
 */
@Data
@TableName("bus_sign_in")
public class SignIn {

    /** 签到ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 签到人ID */
    @TableField("user_id")
    private Long userId;

    /** 关联客户ID（拜访签到时填写） */
    @TableField("customer_id")
    private Long customerId;

    /** 客户名称（冗余） */
    @TableField("customer_name")
    private String customerName;

    /** 签到类型 1上午签到 2下午签退 3拜访签到 */
    @TableField("sign_type")
    private Integer signType;

    /** 纬度 */
    @TableField("latitude")
    private BigDecimal latitude;

    /** 经度 */
    @TableField("longitude")
    private BigDecimal longitude;

    /** 签到地址 */
    @TableField("address")
    private String address;

    /** 备注 */
    @TableField("remark")
    private String remark;

    /** 签到时间 */
    @TableField("sign_time")
    private LocalDateTime signTime;

    /** 创建时间 */
    @TableField("create_time")
    private LocalDateTime createTime;
}
