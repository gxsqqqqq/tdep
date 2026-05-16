package com.tdep.evidence.hash;

import lombok.Builder;
import lombok.Data;

/**
 * Hash 计算结果。
 */
@Data
@Builder
public class HashResult {

    /**
     * Hash 算法。
     */
    private String algorithm;

    /**
     * Hash 值。
     */
    private String hashValue;

    /**
     * 文件大小。
     */
    private long fileSize;
}
