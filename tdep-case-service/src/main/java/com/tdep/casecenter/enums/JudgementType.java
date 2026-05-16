package com.tdep.casecenter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 裁判文书类型枚举。
 */
@Getter
@RequiredArgsConstructor
public enum JudgementType {

    /**
     * 判决书。
     */
    JUDGEMENT("判决书"),

    /**
     * 调解书。
     */
    MEDIATION("调解书"),

    /**
     * 裁定书。
     */
    RULING("裁定书");

    /**
     * 类型中文描述。
     */
    private final String description;
}
