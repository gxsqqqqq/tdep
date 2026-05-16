package com.tdep.casecenter.workflow;

import com.tdep.casecenter.enums.CaseRole;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import com.tdep.common.security.SecurityContextUtil;
import com.tdep.common.security.TdepUserPrincipal;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 案件状态机角色解析器，根据 JWT 中的角色信息推导案件业务角色。
 */
@Component
public class CasePermissionChecker {

    /**
     * 解析当前用户案件业务角色。
     *
     * @return 当前用户案件角色集合
     */
    public Set<CaseRole> currentCaseRoles() {
        TdepUserPrincipal principal = currentPrincipal();
        List<String> roles = principal.getRoles() == null ? List.of() : principal.getRoles();
        Set<CaseRole> caseRoles = new HashSet<>();
        if (containsRole(roles, "ADMIN")) {
            caseRoles.add(CaseRole.ADMIN);
        }
        if (containsRole(roles, "JUDGE")) {
            caseRoles.add(CaseRole.JUDGE);
        }
        if (containsRole(roles, "CLERK")) {
            caseRoles.add(CaseRole.CLERK);
        }
        if (containsRole(roles, "PARTY") || containsRole(roles, "USER") || caseRoles.isEmpty()) {
            caseRoles.add(CaseRole.PARTY);
        }
        return caseRoles;
    }

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
     * 判断当前用户是否拥有指定权限。
     *
     * @param permission 权限编码
     * @return true 表示拥有权限
     */
    public boolean hasPermission(String permission) {
        TdepUserPrincipal principal = currentPrincipal();
        return principal.getPermissions() != null && principal.getPermissions().contains(permission);
    }

    /**
     * 判断角色列表是否包含指定角色。
     *
     * @param roles     JWT 角色集合
     * @param roleCode 角色编码
     * @return true 表示包含
     */
    private boolean containsRole(List<String> roles, String roleCode) {
        return roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role.substring("ROLE_".length()) : role)
                .anyMatch(roleCode::equals);
    }
}
