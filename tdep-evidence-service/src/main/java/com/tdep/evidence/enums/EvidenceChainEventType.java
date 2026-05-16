package com.tdep.evidence.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 证据链事件类型枚举。
 */
@Getter
@RequiredArgsConstructor
public enum EvidenceChainEventType {

    /**
     * 证据上传。
     */
    UPLOAD("证据上传"),

    /**
     * Hash 计算。
     */
    HASH_CALCULATED("Hash计算"),

    /**
     * 对象存储。
     */
    STORED("对象存储"),

    /**
     * 可信时间戳。
     */
    TIMESTAMPED("可信时间戳"),

    /**
     * PDF 固化。
     */
    SEALED("PDF固化"),

    /**
     * 完整性校验。
     */
    VERIFIED("完整性校验"),

    /**
     * 下载。
     */
    DOWNLOADED("下载"),

    /**
     * 冻结。
     */
    FROZEN("冻结"),

    /**
     * 删除。
     */
    DELETED("删除");

    /**
     * 事件中文描述。
     */
    private final String description;
}
