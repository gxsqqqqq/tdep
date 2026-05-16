package com.tdep.evidence.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 证据上传表单请求参数。
 */
@Data
public class EvidenceUploadRequest {

    /**
     * 案件编号。
     */
    @NotNull(message = "不能为空")
    private Long caseId;

    /**
     * 证据说明。
     */
    @Size(max = 500, message = "长度不能超过 500 位")
    private String description;
}
