package com.tdep.auth.service;

import com.tdep.common.security.jwt.JwtClaims;

/**
 * Token 缓存服务，负责 Redis 中 JWT 登录态维护。
 */
public interface TokenCacheService {

    /**
     * 保存用户有效 Token。
     *
     * @param token     JWT 字符串
     * @param claims    JWT 载荷
     * @param ttlSecond 有效期秒数
     */
    void cacheToken(String token, JwtClaims claims, long ttlSecond);

    /**
     * 判断 Token 是否处于有效登录态。
     *
     * @param token  JWT 字符串
     * @param claims JWT 载荷
     * @return true 表示有效
     */
    boolean isTokenActive(String token, JwtClaims claims);

    /**
     * 删除用户有效 Token。
     *
     * @param token  JWT 字符串
     * @param claims JWT 载荷
     */
    void removeToken(String token, JwtClaims claims);
}
