package com.crm.customer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签实体类
 *
 * @author CRM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cus_tag")
public class Tag extends BaseEntity {

    /** 标签ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标签名称 */
    @TableField("tag_name")
    private String tagName;

    /** 标签颜色 */
    @TableField("tag_color")
    private String tagColor;

    /** 标签类型（1系统 2自定义） */
    @TableField("tag_type")
    private Integer tagType;
}
