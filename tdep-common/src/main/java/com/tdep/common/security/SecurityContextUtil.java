package com.tdep.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * 当前登录用户上下文工具，供后续业务代码获取认证主体。
 */
public final class SecurityContextUtil {

    private SecurityContextUtil() {
        // 工具类禁止实例化。
    }

    /**
     * 获取当前认证用户。
     *
     * @return 当前用户主体
     */
    public static Optional<TdepUserPrincipal> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof TdepUserPrincipal principal)) {
            return Optional.empty();
        }
        return Optional.of(principal);
    }

    /**
     * 获取当前用户编号。
     *
     * @return 用户编号
     */
    public static Optional<Long> currentUserId() {
        return currentUser().map(TdepUserPrincipal::getUserId);
    }
}
