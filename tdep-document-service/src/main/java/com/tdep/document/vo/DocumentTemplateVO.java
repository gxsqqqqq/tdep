package com.tdep.document.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DocumentTemplateVO {

    private Long id;

    private String templateCode;

    private String documentType;

    private String templateName;

    private Integer version;

    private String bucketName;

    private String objectKey;

    private String engineType;

    private Integer enabled;

    private Integer currentVersion;

    private String previewUrl;

    private List<String> placeholders;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
