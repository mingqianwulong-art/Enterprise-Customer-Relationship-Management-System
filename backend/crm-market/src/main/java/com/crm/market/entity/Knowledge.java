package com.crm.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识库实体类
 *
 * @author CRM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("market_knowledge")
public class Knowledge extends BaseEntity {

    /** 知识ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    private String title;

    /** 分类 */
    private String category;

    /** 内容（TEXT） */
    private String content;

    /** 标签 */
    private String tags;

    /** 浏览次数 */
    private Integer viewCount;

    /** 状态（0下架 1上架） */
    private Integer status;
}
