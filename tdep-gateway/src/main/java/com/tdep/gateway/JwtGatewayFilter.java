package com.tdep.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 网关 JWT 全局过滤器，负责在流量进入业务服务前完成令牌校验与用户上下文透传。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtGatewayFilter implements GlobalFilter, Ordered {

    private static final String USER_ID_CLAIM = "uid";

    private static final String USERNAME_CLAIM = "username";

    private static final String ROLES_CLAIM = "roles";

    private static final String PERMISSIONS_CLAIM = "permissions";

    private static final String USER_ID_HEADER = "X-TDEP-User-Id";

    private static final String USERNAME_HEADER = "X-TDEP-Username";

    private static final String ROLES_HEADER = "X-TDEP-Roles";

    private static final String PERMISSIONS_HEADER = "X-TDEP-Permissions";

    private final GatewaySecurityProperties securityProperties;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (isPermitAll(path)) {
            return chain.filter(exchange);
        }

        String authorization = exchange.getRequest().getHeaders().getFirst(securityProperties.getHeader());
        String token = resolveBearerToken(authorization);
        if (!StringUtils.hasText(token)) {
            return writeUnauthorized(exchange, "未认证或认证已失效");
        }

        try {
            Claims claims = parseToken(token);
            ServerHttpRequest request = exchange.getRequest()
                    .mutate()
                    .header(USER_ID_HEADER, String.valueOf(readLong(claims, USER_ID_CLAIM)))
                    .header(USERNAME_HEADER, claims.get(USERNAME_CLAIM, String.class))
                    .header(ROLES_HEADER, String.join(",", readStringList(claims, ROLES_CLAIM)))
                    .header(PERMISSIONS_HEADER, String.join(",", readStringList(claims, PERMISSIONS_CLAIM)))
                    .build();
            return chain.filter(exchange.mutate().request(request).build());
        } catch (JwtException | IllegalArgumentException exception) {
            log.warn("网关 JWT 校验失败：{}", exception.getMessage());
            return writeUnauthorized(exchange, "未认证或认证已失效");
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 判断当前路径是否为白名单路径。
     *
     * @param path 请求路径
     * @return 是否放行
     */
    private boolean isPermitAll(String path) {
        return securityProperties.getPermitAllUrls()
                .stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * 从 Authorization 请求头解析 Bearer Token。
     *
     * @param authorization Authorization 请求头
     * @return JWT 字符串
     */
    private String resolveBearerToken(String authorization) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(securityProperties.getTokenPrefix())) {
            return "";
        }
        return authorization.substring(securityProperties.getTokenPrefix().length());
    }

    /**
     * 解析并校验 JWT。
     *
     * @param token JWT 字符串
     * @return JWT Claims
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey())
                .requireIssuer(securityProperties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 构造 JWT 签名密钥。
     *
     * @return 签名密钥
     */
    private SecretKey secretKey() {
        String secret = securityProperties.getSecret();
        if (!StringUtils.hasText(secret)) {
            secret = "tdep-development-secret-key-please-change-in-production";
        }
        if (secret.length() < 32) {
            secret = secret.repeat((32 / secret.length()) + 1);
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 输出网关统一 401 响应。
     *
     * @param exchange WebFlux 请求上下文
     * @param message  响应消息
     * @return 响应完成信号
     */
    private Mono<Void> writeUnauthorized(ServerWebExchange exchange, String message) {
        String body = """
                {"code":401,"message":"%s","data":null}
                """.formatted(message);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(body.getBytes(StandardCharsets.UTF_8))));
    }

    /**
     * 从 JWT Claims 中读取字符串集合。
     *
     * @param claims JWT 载荷
     * @param key    字段名
     * @return 字符串集合
     */
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
