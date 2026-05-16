package com.tdep.common.security.jwt;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 平台 JWT 载荷模型，统一定义令牌内保存的用户身份与权限信息。
 */
@Data
@Builder
public class JwtClaims {

    /**
     * 用户唯一编号。
     */
    private Long userId;

    /**
     * 登录用户名。
     */
    private String username;

    /**
     * 角色编码集合。
     */
    private List<String> roles;

    /**
     * 权限编码集合。
     */
    private List<String> permissions;
}
