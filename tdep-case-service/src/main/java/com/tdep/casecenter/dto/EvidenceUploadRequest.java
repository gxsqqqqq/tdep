package com.tdep.casecenter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 上传证据元数据请求参数。
 */
@Data
public class EvidenceUploadRequest {

    /**
     * 证据名称。
     */
    @NotBlank(message = "不能为空")
    @Size(max = 200, message = "长度不能超过 200 位")
    private String evidenceName;

    /**
     * 证据类型。
     */
    @NotBlank(message = "不能为空")
    @Size(max = 64, message = "长度不能超过 64 位")
    private String evidenceType;

    /**
     * 文件地址。
     */
    @NotBlank(message = "不能为空")
    @Size(max = 500, message = "长度不能超过 500 位")
    private String fileUrl;

    /**
     * 文件哈希。
     */
    @Size(max = 128, message = "长度不能超过 128 位")
    private String fileHash;

    /**
     * 证据说明。
     */
    @Size(max = 500, message = "长度不能超过 500 位")
    private String description;
}
