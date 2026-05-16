package com.tdep.document.security;

import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import com.tdep.common.security.SecurityContextUtil;
import com.tdep.common.security.TdepUserPrincipal;
import com.tdep.document.enums.DocumentRole;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DocumentPermissionChecker {

    public Set<DocumentRole> currentDocumentRoles() {
        TdepUserPrincipal principal = currentPrincipal();
        List<String> roles = principal.getRoles() == null ? List.of() : principal.getRoles();
        Set<DocumentRole> documentRoles = new HashSet<>();
        if (containsRole(roles, "ADMIN")) {
            documentRoles.add(DocumentRole.ADMIN);
        }
        if (containsRole(roles, "JUDGE") || containsRole(roles, "JUDICIAL_OFFICER")) {
            documentRoles.add(DocumentRole.JUDGE);
        }
        if (containsRole(roles, "CLERK")) {
            documentRoles.add(DocumentRole.CLERK);
        }
        if (containsRole(roles, "PARTY") || containsRole(roles, "USER")
                || containsRole(roles, "LAWYER") || containsRole(roles, "ENTERPRISE_USER")
                || containsRole(roles, "INDIVIDUAL_USER") || documentRoles.isEmpty()) {
            documentRoles.add(DocumentRole.PARTY);
        }
        return documentRoles;
    }

    public TdepUserPrincipal currentPrincipal() {
        return SecurityContextUtil.currentUser()
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED));
    }

    public Long currentUserId() {
        return currentPrincipal().getUserId();
    }

    private boolean containsRole(List<String> roles, String roleCode) {
        return roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role.substring("ROLE_".length()) : role)
                .anyMatch(roleCode::equals);
    }
}
