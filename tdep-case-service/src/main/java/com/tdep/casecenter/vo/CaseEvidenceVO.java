package com.tdep.casecenter.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 案件证据响应视图。
 */
@Data
@Builder
public class CaseEvidenceVO {

    /**
     * 主键。
     */
    private Long id;

    /**
     * 证据名称。
     */
    private String evidenceName;

    /**
     * 证据类型。
     */
    private String evidenceType;

    /**
     * 文件地址。
     */
    private String fileUrl;

    /**
     * 文件哈希。
     */
    private String fileHash;

    /**
     * 证据说明。
     */
    private String description;

    /**
     * 上传人编号。
     */
    private Long uploadedBy;

    /**
     * 上传时间。
     */
    private LocalDateTime uploadedAt;
}
