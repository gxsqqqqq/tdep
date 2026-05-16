package com.tdep.auth.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 登录成功响应视图。
 */
@Data
@Builder
public class LoginVO {

    /**
     * JWT 访问令牌。
     */
    private String accessToken;

    /**
     * 令牌类型。
     */
    private String tokenType;

    /**
     * 令牌有效期，单位秒。
     */
    private Long expiresIn;

    /**
     * 当前用户信息。
     */
    private UserInfoVO user;
}
