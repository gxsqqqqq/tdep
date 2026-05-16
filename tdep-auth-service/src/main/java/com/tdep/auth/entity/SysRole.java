package com.tdep.auth.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统角色实体，对应 sys_role 表。
 */
@Data
@TableName("sys_role")
public class SysRole implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色主键。
     */
    @TableId
    private Long id;

    /**
     * 角色编码，用于 Spring Security 的 ROLE_ 权限。
     */
    private String roleCode;

    /**
     * 角色名称。
     */
    private String roleName;

    /**
     * 角色说明。
     */
    private String description;

    /**
     * 角色状态：1 启用，0 禁用。
     */
    private Integer status;

    /**
     * 创建时间。
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标记：0 未删除，1 已删除。
     */
    @TableLogic
    private Integer deleted;
}
