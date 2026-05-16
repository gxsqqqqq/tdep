package com.tdep.common.security.jwt;

/**
 * JWT 令牌状态校验扩展点，用于接入 Redis 黑白名单、单点登录等服务侧校验。
 */
public interface JwtTokenValidator {

    /**
     * 判断令牌是否仍然有效。
     *
     * @param token  JWT 字符串
     * @param claims 已解析的令牌载荷
     * @return true 表示令牌可继续使用
     */
    boolean isTokenActive(String token, JwtClaims claims);
}
