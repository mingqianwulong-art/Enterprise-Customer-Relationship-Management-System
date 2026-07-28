package com.crm.customer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 联系人实体类
 *
 * @author CRM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cus_contact")
public class Contact extends BaseEntity {

    /** 联系人ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 联系人姓名 */
    @TableField("name")
    private String name;

    /** 职位 */
    @TableField("position")
    private String position;

    /** 手机号 */
    @TableField("phone")
    private String phone;

    /** 邮箱 */
    @TableField("email")
    private String email;

    /** 是否首要联系人（0否 1是） */
    @TableField("is_primary")
    private Integer isPrimary;
}
