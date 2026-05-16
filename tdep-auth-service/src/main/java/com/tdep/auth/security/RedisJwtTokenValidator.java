package com.tdep.auth.security;

import com.tdep.auth.service.TokenCacheService;
import com.tdep.common.security.jwt.JwtClaims;
import com.tdep.common.security.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Redis JWT 登录态校验器，确保已退出或未缓存的 Token 不能继续访问接口。
 */
@Order(0)
@Component
@RequiredArgsConstructor
public class RedisJwtTokenValidator implements JwtTokenValidator {

    private final TokenCacheService tokenCacheService;

    @Override
    public boolean isTokenActive(String token, JwtClaims claims) {
        return tokenCacheService.isTokenActive(token, claims);
    }
}
