package com.tdep.document.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentArchiveVO {

    private Long id;

    private Long documentId;

    private Long caseId;

    private String archiveNo;

    private String bucketName;

    private String objectKey;

    private String fileHash;

    private Long fileSize;

    private Long archivedBy;

    private LocalDateTime archivedAt;

    private Integer retentionYears;

    private String downloadUrl;
}
