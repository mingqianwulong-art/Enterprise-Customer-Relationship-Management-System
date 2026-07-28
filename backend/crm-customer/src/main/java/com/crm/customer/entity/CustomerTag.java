package com.crm.customer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 客户标签关联实体类
 *
 * @author CRM
 */
@Data
@TableName("cus_customer_tag")
public class CustomerTag {

    /** 客户ID */
    @TableField("customer_id")
    private Long customerId;

    /** 标签ID */
    @TableField("tag_id")
    private Long tagId;
}
