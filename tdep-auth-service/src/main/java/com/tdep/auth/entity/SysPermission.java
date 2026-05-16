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
 * 系统权限实体，对应 sys_permission 表。
 */
@Data
@TableName("sys_permission")
public class SysPermission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 权限主键。
     */
    @TableId
    private Long id;

    /**
     * 权限编码，推荐格式为 模块:资源:动作。
     */
    private String permissionCode;

    /**
     * 权限名称。
     */
    private String permissionName;

    /**
     * 权限类型：MENU 菜单，BUTTON 按钮，API 接口。
     */
    private String permissionType;

    /**
     * 权限资源路径，可保存前端路由或后端接口地址。
     */
    private String resourcePath;

    /**
     * HTTP 方法，API 权限可填写 GET、POST 等。
     */
    private String httpMethod;

    /**
     * 权限说明。
     */
    private String description;

    /**
     * 权限状态：1 启用，0 禁用。
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
