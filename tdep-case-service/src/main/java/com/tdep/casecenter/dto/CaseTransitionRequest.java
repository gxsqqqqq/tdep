package com.tdep.casecenter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 案件状态流转请求参数。
 */
@Data
public class CaseTransitionRequest {

    /**
     * 状态流转动作。
     */
    @NotBlank(message = "不能为空")
    private String action;

    /**
     * 处理意见。
     */
    private String comment;
}
