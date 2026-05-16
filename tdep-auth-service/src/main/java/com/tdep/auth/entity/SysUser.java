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
 * 系统用户实体，对应 sys_user 表。
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户主键。
     */
    @TableId
    private Long id;

    /**
     * 登录用户名。
     */
    private String username;

    /**
     * BCrypt 加密后的密码。
     */
    private String password;

    /**
     * 用户昵称。
     */
    private String nickname;

    /**
     * 手机号码。
     */
    private String phone;

    /**
     * 邮箱地址。
     */
    private String email;

    /**
     * 账号状态：1 启用，0 禁用。
     */
    private Integer status;

    /**
     * 最后登录时间。
     */
    private LocalDateTime lastLoginTime;

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
