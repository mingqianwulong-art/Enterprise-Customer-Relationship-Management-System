package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门实体类
 *
 * @author CRM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    /** 部门ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父部门ID */
    @TableField("parent_id")
    private Long parentId;

    /** 祖级列表 */
    @TableField("ancestors")
    private String ancestors;

    /** 部门名称 */
    @TableField("dept_name")
    private String deptName;

    /** 排序 */
    @TableField("order_num")
    private Integer orderNum;

    /** 状态（0停用 1启用） */
    @TableField("status")
    private Integer status;
}
