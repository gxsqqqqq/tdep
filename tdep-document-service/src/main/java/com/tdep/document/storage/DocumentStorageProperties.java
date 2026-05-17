package com.tdep.document.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tdep.document.storage")
public class DocumentStorageProperties {

    private String endpoint = "oss-cn-hangzhou.aliyuncs.com";

    private String accessKeyId = "your-access-key-id";

    private String accessKeySecret = "your-access-key-secret";

    private String templateBucket = "tdep-document-template";

    private String wordBucket = "tdep-document-word";

    private String pdfBucket = "tdep-document-pdf";

    private String signedBucket = "tdep-document-signed";

    private String archiveBucket = "tdep-document-archive";

    private int presignedUrlSeconds = 900;
}
