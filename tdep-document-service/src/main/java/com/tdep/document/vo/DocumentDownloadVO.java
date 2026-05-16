package com.tdep.document.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentDownloadVO {

    private Long documentId;

    private String fileName;

    private String bucketName;

    private String objectKey;

    private String downloadUrl;
}
