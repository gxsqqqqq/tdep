package com.tdep.casecenter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 案件当事人类型枚举。
 */
@Getter
@RequiredArgsConstructor
public enum CasePartyType {

    /**
     * 原告。
     */
    PLAINTIFF("原告"),

    /**
     * 被告。
     */
    DEFENDANT("被告"),

    /**
     * 第三人。
     */
    THIRD_PARTY("第三人");

    /**
     * 类型中文描述。
     */
    private final String description;
}
