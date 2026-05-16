package com.tdep.evidence.security;

import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import com.tdep.common.security.SecurityContextUtil;
import com.tdep.common.security.TdepUserPrincipal;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 证据模块权限辅助组件，负责从 JWT 上下文解析当前用户角色和权限。
 */
@Component
public class EvidencePermissionChecker {

    /**
     * 获取当前认证用户。
     *
     * @return 当前用户主体
     */
    public TdepUserPrincipal currentPrincipal() {
        return SecurityContextUtil.currentUser()
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED));
    }

    /**
     * 判断当前用户是否有指定权限。
     *
     * @param permission 权限编码
     * @return true 表示有权限
     */
    public boolean hasPermission(String permission) {
        TdepUserPrincipal principal = currentPrincipal();
        return principal.getPermissions() != null && principal.getPermissions().contains(permission);
    }

    /**
     * 获取当前用户用于审计展示的业务角色。
     *
     * @return 角色编码
     */
    public String currentRole() {
        List<String> roles = currentPrincipal().getRoles();
        if (roles == null || roles.isEmpty()) {
            return "PARTY";
        }
        if (containsRole(roles, "ADMIN")) {
            return "ADMIN";
        }
        if (containsRole(roles, "JUDGE")) {
            return "JUDGE";
        }
        return "PARTY";
    }

    /**
     * 判断当前用户是否为管理员。
     *
     * @return true 表示管理员
     */
    public boolean isAdmin() {
        return "ADMIN".equals(currentRole());
    }

    /**
     * 判断当前用户是否为法官。
     *
     * @return true 表示法官
     */
    public boolean isJudge() {
        return "JUDGE".equals(currentRole());
    }

    /**
     * 判断角色列表是否包含指定角色。
     *
     * @param roles    角色列表
     * @param roleCode 角色编码
     * @return true 表示包含
     */
    private boolean containsRole(List<String> roles, String roleCode) {
        return roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role.substring("ROLE_".length()) : role)
                .anyMatch(roleCode::equals);
    }
}
