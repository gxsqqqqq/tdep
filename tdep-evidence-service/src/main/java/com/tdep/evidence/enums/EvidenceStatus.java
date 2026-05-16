package com.tdep.evidence.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 电子证据生命周期状态枚举。
 */
@Getter
@RequiredArgsConstructor
public enum EvidenceStatus {

    /**
     * 上传中。
     */
    UPLOADING("上传中"),

    /**
     * Hash 计算中。
     */
    HASHING("Hash计算中"),

    /**
     * 已存储。
     */
    STORED("已存储"),

    /**
     * 已时间戳。
     */
    TIMESTAMPED("已时间戳"),

    /**
     * 已固化。
     */
    SEALED("已固化"),

    /**
     * 校验中。
     */
    VERIFYING("校验中"),

    /**
     * 校验通过。
     */
    VERIFIED("校验通过"),

    /**
     * 校验失败。
     */
    VERIFY_FAILED("校验失败"),

    /**
     * 已删除。
     */
    DELETED("已删除"),

    /**
     * 已冻结。
     */
    FROZEN("已冻结");

    /**
     * 状态中文描述。
     */
    private final String description;
}
