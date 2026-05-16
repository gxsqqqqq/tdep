package com.tdep.evidence.hash;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Hash 校验结果。
 */
@Data
@Builder
public class HashVerifyResult {

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
    private boolean matched;

    /**
     * 校验时间。
     */
    private LocalDateTime verifyTime;
}
