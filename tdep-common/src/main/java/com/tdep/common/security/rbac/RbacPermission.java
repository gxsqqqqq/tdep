package com.tdep.common.security.rbac;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * RBAC 权限编码枚举，后续业务接口可通过 @PreAuthorize 使用这些编码。
 */
@Getter
@RequiredArgsConstructor
public enum RbacPermission {

    /**
     * 用户管理权限。
     */
    USER_MANAGE("user:manage", "用户管理"),

    /**
     * 证据管理权限。
     */
    EVIDENCE_MANAGE("evidence:manage", "电子证据管理"),

    /**
     * 案件管理权限。
     */
    CASE_MANAGE("case:manage", "案件管理"),

    /**
     * 法律文书管理权限。
     */
    DOCUMENT_MANAGE("document:manage", "法律文书管理"),

    /**
     * AI 法律助手使用权限。
     */
    AI_ASSISTANT_USE("ai:assistant:use", "AI 法律助手"),

    /**
     * 区块链存证权限，预留给后续扩展。
     */
    BLOCKCHAIN_ANCHOR("blockchain:anchor", "区块链存证");

    /**
     * 权限编码。
     */
    private final String code;

    /**
     * 权限名称。
     */
    private final String name;
}
