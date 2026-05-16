package com.tdep.evidence.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 证据文件响应视图。
 */
@Data
@Builder
public class EvidenceVO {

    /**
     * 证据主键。
     */
    private Long id;

    /**
     * 证据编号。
     */
    private String evidenceNo;

    /**
     * 案件编号。
     */
    private Long caseId;

    /**
     * 上传人编号。
     */
    private Long uploaderId;

    /**
     * 文件名。
     */
    private String fileName;

    /**
     * 文件扩展名。
     */
    private String fileExt;

    /**
     * 内容类型。
     */
    private String contentType;

    /**
     * 文件大小。
     */
    private Long fileSize;

    /**
     * 证据状态。
     */
    private String status;

    /**
     * 证据说明。
     */
    private String description;

    /**
     * 上传时间。
     */
    private LocalDateTime uploadedAt;

    /**
     * 固化时间。
     */
    private LocalDateTime sealedAt;

    /**
     * Hash 信息。
     */
    private EvidenceHashVO hash;
}
