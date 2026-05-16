package com.tdep.evidence.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 证据操作类型枚举。
 */
@Getter
@RequiredArgsConstructor
public enum EvidenceOperationType {

    /**
     * 上传。
     */
    UPLOAD("上传"),

    /**
     * 查询。
     */
    READ("查询"),

    /**
     * 下载。
     */
    DOWNLOAD("下载"),

    /**
     * Hash 查询。
     */
    HASH_READ("Hash查询"),

    /**
     * 文件校验。
     */
    VERIFY("文件校验"),

    /**
     * PDF 固化。
     */
    SEAL("PDF固化"),

    /**
     * 查询证据链。
     */
    CHAIN_READ("查询证据链"),

    /**
     * 校验证据链。
     */
    CHAIN_VERIFY("校验证据链"),

    /**
     * 冻结。
     */
    FREEZE("冻结"),

    /**
     * 删除。
     */
    DELETE("删除");

    /**
     * 类型中文描述。
     */
    private final String description;
}
