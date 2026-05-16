package com.tdep.evidence.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 证据 Hash 响应视图。
 */
@Data
@Builder
public class EvidenceHashVO {

    /**
     * 证据主键。
     */
    private Long evidenceId;

    /**
     * Hash 算法。
     */
    private String hashAlgorithm;

    /**
     * Hash 值。
     */
    private String hashValue;

    /**
     * 文件唯一指纹。
     */
    private String fileFingerprint;

    /**
     * 文件大小。
     */
    private Long fileSize;

    /**
     * Hash 计算时间。
     */
    private LocalDateTime calculatedAt;

    /**
     * 最近校验时间。
     */
    private LocalDateTime verifiedAt;

    /**
     * 最近校验结果。
     */
    private Integer verifyResult;
}
