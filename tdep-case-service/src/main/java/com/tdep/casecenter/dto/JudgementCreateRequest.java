package com.tdep.casecenter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建判决记录请求参数。
 */
@Data
public class JudgementCreateRequest {

    /**
     * 文书类型。
     */
    @NotBlank(message = "不能为空")
    @Size(max = 32, message = "长度不能超过 32 位")
    private String judgementType;

    /**
     * 文书标题。
     */
    @NotBlank(message = "不能为空")
    @Size(max = 200, message = "长度不能超过 200 位")
    private String title;

    /**
     * 裁判摘要。
     */
    private String contentSummary;

    /**
     * 文书文件地址。
     */
    @Size(max = 500, message = "长度不能超过 500 位")
    private String documentUrl;

    /**
     * 出具法官编号。
     */
    @NotNull(message = "不能为空")
    private Long judgeId;

    /**
     * 生效时间。
     */
    private LocalDateTime effectiveTime;

    /**
     * 上诉截止时间。
     */
    private LocalDateTime appealDeadline;
}
