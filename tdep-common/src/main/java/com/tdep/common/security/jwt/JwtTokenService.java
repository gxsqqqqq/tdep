package com.tdep.common.security.jwt;

import com.tdep.common.security.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * JWT 令牌服务，负责签发和解析访问令牌。
 */
@Component
public class JwtTokenService {

    private static final String USER_ID_CLAIM = "uid";

    private static final String USERNAME_CLAIM = "username";

    private static final String ROLES_CLAIM = "roles";

    private static final String PERMISSIONS_CLAIM = "permissions";

    private final SecurityProperties securityProperties;

    private final SecretKey secretKey;

    /**
     * 初始化 JWT 服务。
     *
     * @param securityProperties 安全配置
     */
    public JwtTokenService(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        this.secretKey = Keys.hmacShaKeyFor(normalizedSecret(securityProperties.getJwt().getSecret()));
    }

    /**
     * 签发访问令牌。
     *
     * @param jwtClaims 平台令牌载荷
     * @return JWT 字符串
     */
    public String generateAccessToken(JwtClaims jwtClaims) {
        Instant now = Instant.now();
        Instant expireAt = now.plusSeconds(securityProperties.getJwt().getAccessTokenSeconds());
        return Jwts.builder()
                .issuer(securityProperties.getJwt().getIssuer())
                .subject(String.valueOf(jwtClaims.getUserId()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expireAt))
                .claim(USER_ID_CLAIM, jwtClaims.getUserId())
                .claim(USERNAME_CLAIM, jwtClaims.getUsername())
                .claim(ROLES_CLAIM, jwtClaims.getRoles())
                .claim(PERMISSIONS_CLAIM, jwtClaims.getPermissions())
                .signWith(secretKey)
                .compact();
    }

    /**
     * 解析并校验访问令牌。
     *
     * @param token JWT 字符串
     * @return 平台令牌载荷
     */
    public JwtClaims parseAccessToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(securityProperties.getJwt().getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return JwtClaims.builder()
                .userId(readLong(claims, USER_ID_CLAIM))
                .username(claims.get(USERNAME_CLAIM, String.class))
                .roles(readStringList(claims, ROLES_CLAIM))
                .permissions(readStringList(claims, PERMISSIONS_CLAIM))
                .build();
    }

    /**
     * 从 Authorization 请求头中解析 Bearer 令牌。
     *
     * @param authorizationHeader Authorization 请求头
     * @return JWT 字符串，无法解析时返回空字符串
     */
    public String resolveBearerToken(String authorizationHeader) {
        String tokenPrefix = securityProperties.getJwt().getTokenPrefix();
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(tokenPrefix)) {
            return "";
        }
        return authorizationHeader.substring(tokenPrefix.length());
    }

    /**
     * 确保 HMAC 密钥长度满足 JWT 算法要求。
     *
     * @param secret 配置密钥
     * @return 规范化后的字节数组
     */
    private byte[] normalizedSecret(String secret) {
        String normalized = StringUtils.hasText(secret) ? secret : "tdep-development-secret-key-please-change-in-production";
        if (normalized.length() < 32) {
            normalized = normalized.repeat((32 / normalized.length()) + 1);
        }
        return normalized.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 从 JWT Claims 中读取字符串集合。
     *
     * @param claims JWT 载荷
     * @param key    字段名
     * @return 字符串集合
     */
    @SuppressWarnings("unchecked")
    private List<String> readStringList(Claims claims, String key) {
        Object value = claims.get(key);
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of();
    }

    /**
     * 从 JWT Claims 中读取长整型字段。
     *
     * @param claims JWT 载荷
     * @param key    字段名
     * @return 长整型字段值
     */
    private Long readLong(Claims claims, String key) {
        Object value = claims.get(key);
        if (value instanceof Number number) {
            return number.longValue();
        }
        return value == null ? null : Long.valueOf(String.valueOf(value));
    }
}
