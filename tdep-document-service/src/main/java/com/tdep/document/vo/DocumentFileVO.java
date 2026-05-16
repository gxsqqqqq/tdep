package com.tdep.document.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentFileVO {

    private Long id;

    private Long caseId;

    private String documentNo;

    private String documentType;

    private String title;

    private String status;

    private Long templateId;

    private String wordObjectKey;

    private String pdfObjectKey;

    private String signedObjectKey;

    private String fileHash;

    private Long createdBy;

    private Long reviewedBy;

    private Long signedBy;

    private LocalDateTime generatedAt;

    private LocalDateTime reviewedAt;

    private LocalDateTime signedAt;

    private LocalDateTime effectiveAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
