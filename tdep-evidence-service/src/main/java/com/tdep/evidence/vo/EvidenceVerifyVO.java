package com.tdep.evidence.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 证据完整性校验响应视图。
 */
@Data
@Builder
public class EvidenceVerifyVO {

    /**
     * 证据主键。
     */
    private Long evidenceId;

    /**
     * Hash 算法。
     */
    private String algorithm;

    /**
     * 原始 Hash。
     */
    private String originalHash;

    /**
     * 当前 Hash。
     */
    private String currentHash;

    /**
     * 是否匹配。
     */
    private Boolean matched;

    /**
     * 校验后的状态。
     */
    private String status;

    /**
     * 校验时间。
     */
    private LocalDateTime verifyTime;
}
