package com.tdep.auth.serviceImpl;

import com.tdep.auth.service.TokenCacheService;
import com.tdep.common.security.jwt.JwtClaims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HexFormat;

/**
 * Redis Token 缓存服务实现，使用 Token SHA-256 摘要作为键避免明文 Token 暴露在 Redis 键中。
 */
@Service
@RequiredArgsConstructor
public class RedisTokenCacheServiceImpl implements TokenCacheService {

    private static final String TOKEN_KEY_PREFIX = "tdep:auth:token:";

    private static final String USER_TOKEN_KEY_PREFIX = "tdep:auth:user-token:";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void cacheToken(String token, JwtClaims claims, long ttlSecond) {
        if (!StringUtils.hasText(token) || claims == null || claims.getUserId() == null) {
            return;
        }
        Duration ttl = Duration.ofSeconds(ttlSecond);
        String tokenKey = tokenKey(token);
        String userTokenKey = userTokenKey(claims.getUserId());
        stringRedisTemplate.opsForValue().set(tokenKey, String.valueOf(claims.getUserId()), ttl);
        stringRedisTemplate.opsForValue().set(userTokenKey, tokenDigest(token), ttl);
    }

    @Override
    public boolean isTokenActive(String token, JwtClaims claims) {
        if (!StringUtils.hasText(token) || claims == null || claims.getUserId() == null) {
            return false;
        }
        String cachedUserId = stringRedisTemplate.opsForValue().get(tokenKey(token));
        String cachedDigest = stringRedisTemplate.opsForValue().get(userTokenKey(claims.getUserId()));
        return String.valueOf(claims.getUserId()).equals(cachedUserId)
                && tokenDigest(token).equals(cachedDigest);
    }

    @Override
    public void removeToken(String token, JwtClaims claims) {
        if (!StringUtils.hasText(token)) {
            return;
        }
        stringRedisTemplate.delete(tokenKey(token));
        if (claims != null && claims.getUserId() != null) {
            stringRedisTemplate.delete(userTokenKey(claims.getUserId()));
        }
    }

    /**
     * 构造 Token 缓存键。
     *
     * @param token JWT 字符串
     * @return Redis 键
     */
    private String tokenKey(String token) {
        return TOKEN_KEY_PREFIX + tokenDigest(token);
    }

    /**
     * 构造用户当前 Token 缓存键。
     *
     * @param userId 用户主键
     * @return Redis 键
     */
    private String userTokenKey(Long userId) {
        return USER_TOKEN_KEY_PREFIX + userId;
    }

    /**
     * 对 Token 做 SHA-256 摘要。
     *
     * @param token JWT 字符串
     * @return 十六进制摘要
     */
    private String tokenDigest(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("当前运行环境不支持 SHA-256 摘要算法", exception);
        }
    }
}
