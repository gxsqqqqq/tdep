package com.tdep.evidence.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 证据冻结请求参数。
 */
@Data
public class EvidenceFreezeRequest {

    /**
     * 冻结原因。
     */
    @NotBlank(message = "不能为空")
    private String reason;
}
