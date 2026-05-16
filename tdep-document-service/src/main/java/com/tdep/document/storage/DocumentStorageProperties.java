package com.tdep.document.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tdep.document.storage")
public class DocumentStorageProperties {

    private String endpoint = "http://localhost:9000";

    private String accessKey = "minioadmin";

    private String secretKey = "minioadmin";

    private String templateBucket = "tdep-document-template";

    private String wordBucket = "tdep-document-word";

    private String pdfBucket = "tdep-document-pdf";

    private String signedBucket = "tdep-document-signed";

    private String archiveBucket = "tdep-document-archive";

    private int presignedUrlSeconds = 900;
}
