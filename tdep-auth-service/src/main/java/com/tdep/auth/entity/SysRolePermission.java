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
 * 角色权限关联实体，对应 sys_role_permission 表。
 */
@Data
@TableName("sys_role_permission")
public class SysRolePermission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关联主键。
     */
    @TableId
    private Long id;

    /**
     * 角色主键。
     */
    private Long roleId;

    /**
     * 权限主键。
     */
    private Long permissionId;

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
