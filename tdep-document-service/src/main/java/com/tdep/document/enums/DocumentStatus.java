package com.tdep.document.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentStatus {

    DRAFT("草稿"),
    GENERATED("已生成"),
    UNDER_REVIEW("审核中"),
    REVIEW_REJECTED("审核驳回"),
    SIGNED("已签章"),
    EFFECTIVE("已生效"),
    ARCHIVED("已归档"),
    VOIDED("已作废");

    private final String description;

    public boolean terminal() {
        return this == ARCHIVED || this == VOIDED;
    }
}
