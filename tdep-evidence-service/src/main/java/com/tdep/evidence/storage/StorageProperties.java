package com.tdep.evidence.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MinIO 对象存储配置属性。
 */
@Data
@ConfigurationProperties(prefix = "tdep.evidence.storage")
public class StorageProperties {

    /**
     * MinIO 服务端地址。
     */
    private String endpoint = "http://localhost:9000";

    /**
     * 访问密钥。
     */
    private String accessKey = "minioadmin";

    /**
     * 访问密钥口令。
     */
    private String secretKey = "minioadmin";

    /**
     * 原始证据 bucket。
     */
    private String originalBucket = "tdep-evidence-original";

    /**
     * 固化凭证 bucket。
     */
    private String sealedBucket = "tdep-evidence-sealed";

    /**
     * 临时 bucket。
     */
    private String tempBucket = "tdep-evidence-temp";

    /**
     * 预览 bucket。
     */
    private String previewBucket = "tdep-evidence-preview";

    /**
     * 预签名 URL 有效期秒数。
     */
    private int presignedUrlSeconds = 900;
}
