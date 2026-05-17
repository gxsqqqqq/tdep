package com.tdep.evidence.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tdep.evidence.storage")
public class StorageProperties {

    private String endpoint = "oss-cn-hangzhou.aliyuncs.com";

    private String accessKeyId = "your-access-key-id";

    private String accessKeySecret = "your-access-key-secret";

    private String originalBucket = "tdep-evidence-original";

    private String sealedBucket = "tdep-evidence-sealed";

    private String tempBucket = "tdep-evidence-temp";

    private String previewBucket = "tdep-evidence-preview";

    private int presignedUrlSeconds = 900;
}
