package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体类
 *
 * @author CRM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /** 角色ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色编码 */
    @TableField("role_code")
    private String roleCode;

    /** 角色名称 */
    @TableField("role_name")
    private String roleName;

    /** 数据范围（1本人 2本部门 3本部门及下 4全部） */
    @TableField("data_scope")
    private Integer dataScope;

    /** 状态（0停用 1启用） */
    @TableField("status")
    private Integer status;

    /** 备注 */
    @TableField("remark")
    private String remark;
}
