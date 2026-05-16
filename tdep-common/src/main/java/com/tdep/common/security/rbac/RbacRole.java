package com.tdep.common.security.rbac;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * RBAC 角色编码枚举，统一约束平台内置角色命名。
 */
@Getter
@RequiredArgsConstructor
public enum RbacRole {

    /**
     * 平台管理员。
     */
    ADMIN("ADMIN", "平台管理员"),

    /**
     * 司法工作人员。
     */
    JUDICIAL_OFFICER("JUDICIAL_OFFICER", "司法工作人员"),

    /**
     * 律师用户。
     */
    LAWYER("LAWYER", "律师"),

    /**
     * 企业用户。
     */
    ENTERPRISE_USER("ENTERPRISE_USER", "企业用户"),

    /**
     * 普通个人用户。
     */
    INDIVIDUAL_USER("INDIVIDUAL_USER", "个人用户");

    /**
     * 角色编码。
     */
    private final String code;

    /**
     * 角色名称。
     */
    private final String name;
}
