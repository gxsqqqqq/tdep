package com.tdep.evidence.dto;

import lombok.Data;

/**
 * 证据 PDF 固化请求参数。
 */
@Data
public class EvidenceSealRequest {

    /**
     * 固化备注。
     */
    private String remark;
}
