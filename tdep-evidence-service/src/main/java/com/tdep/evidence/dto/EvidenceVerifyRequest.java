package com.tdep.evidence.dto;

import lombok.Data;

/**
 * 证据校验请求参数。
 */
@Data
public class EvidenceVerifyRequest {

    /**
     * 校验备注。
     */
    private String remark;
}
