package com.tdep.casecenter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 案件业务角色枚举，用于状态机权限校验。
 */
@Getter
@RequiredArgsConstructor
public enum CaseRole {

    /**
     * 当事人。
     */
    PARTY("当事人"),

    /**
     * 法官。
     */
    JUDGE("法官"),

    /**
     * 书记员。
     */
    CLERK("书记员"),

    /**
     * 管理员。
     */
    ADMIN("管理员");

    /**
     * 角色中文描述。
     */
    private final String description;
}
