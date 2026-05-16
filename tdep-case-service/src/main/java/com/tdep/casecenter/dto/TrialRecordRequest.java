package com.tdep.casecenter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新庭审记录请求参数。
 */
@Data
public class TrialRecordRequest {

    /**
     * 庭审记录内容。
     */
    @NotBlank(message = "不能为空")
    private String trialRecord;

    /**
     * 庭审记录文件地址。
     */
    private String recordUrl;
}
