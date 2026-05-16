package com.tdep.common.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

/**
 * 平台认证用户主体，封装用户、角色和权限信息。
 */
@Getter
@Builder
public class TdepUserPrincipal implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一编号。
     */
    private final Long userId;

    /**
     * 登录用户名。
     */
    private final String username;

    /**
     * 用户角色编码集合。
     */
    private final List<String> roles;

    /**
     * 用户权限编码集合。
     */
    private final List<String> permissions;

    /**
     * Spring Security 权限集合。
     */
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public String getPassword() {
        // JWT 模式下认证态不在上下文中保存密码。
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
