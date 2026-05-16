package com.tdep.casecenter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 庭审状态枚举。
 */
@Getter
@RequiredArgsConstructor
public enum TrialStatus {

    /**
     * 已排期。
     */
    SCHEDULED("已排期"),

    /**
     * 进行中。
     */
    IN_PROGRESS("进行中"),

    /**
     * 已结束。
     */
    FINISHED("已结束"),

    /**
     * 已取消。
     */
    CANCELLED("已取消");

    /**
     * 状态中文描述。
     */
    private final String description;
}
