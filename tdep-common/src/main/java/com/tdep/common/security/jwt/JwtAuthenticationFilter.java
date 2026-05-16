package com.tdep.common.security.jwt;

import com.tdep.common.security.SecurityProperties;
import com.tdep.common.security.TdepUserPrincipal;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT 认证过滤器，从请求头中解析令牌并建立 Spring Security 上下文。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    private final SecurityProperties securityProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(securityProperties.getJwt().getHeader());
        String token = jwtTokenService.resolveBearerToken(authorization);
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            JwtClaims claims = jwtTokenService.parseAccessToken(token);
            TdepUserPrincipal principal = buildPrincipal(claims);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    principal.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException | IllegalArgumentException exception) {
            log.warn("JWT 令牌解析失败：{}", exception.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 根据 JWT 载荷构造认证用户主体。
     *
     * @param claims JWT 载荷
     * @return 认证用户主体
     */
    private TdepUserPrincipal buildPrincipal(JwtClaims claims) {
        List<String> roles = claims.getRoles() == null ? List.of() : claims.getRoles();
        List<String> permissions = claims.getPermissions() == null ? List.of() : claims.getPermissions();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);
        permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);

        return TdepUserPrincipal.builder()
                .userId(claims.getUserId())
                .username(claims.getUsername())
                .roles(roles)
                .permissions(permissions)
                .authorities(authorities)
                .build();
    }
}
