package com.tdep.casecenter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建庭审记录请求参数。
 */
@Data
public class TrialCreateRequest {

    /**
     * 庭审类型。
     */
    @NotBlank(message = "不能为空")
    @Size(max = 32, message = "长度不能超过 32 位")
    private String trialType;

    /**
     * 开庭时间。
     */
    @NotNull(message = "不能为空")
    private LocalDateTime trialTime;

    /**
     * 开庭地点或线上地址。
     */
    @NotBlank(message = "不能为空")
    @Size(max = 255, message = "长度不能超过 255 位")
    private String trialLocation;

    /**
     * 承办法官编号。
     */
    @NotNull(message = "不能为空")
    private Long judgeId;

    /**
     * 书记员编号。
     */
    private Long clerkId;

    /**
     * 庭审记录内容。
     */
    private String trialRecord;

    /**
     * 庭审记录文件地址。
     */
    @Size(max = 500, message = "长度不能超过 500 位")
    private String recordUrl;
}
