package com.tdep.auth.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 当前用户信息响应视图。
 */
@Data
@Builder
public class UserInfoVO {

    /**
     * 用户主键。
     */
    private Long id;

    /**
     * 登录用户名。
     */
    private String username;

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
     * 角色编码集合。
     */
    private List<String> roles;

    /**
     * 权限编码集合。
     */
    private List<String> permissions;

    /**
     * 最后登录时间。
     */
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间。
     */
    private LocalDateTime createdAt;
}
