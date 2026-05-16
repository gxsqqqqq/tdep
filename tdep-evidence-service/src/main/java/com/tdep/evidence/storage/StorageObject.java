package com.tdep.evidence.storage;

import lombok.Builder;
import lombok.Data;

/**
 * 对象存储写入结果。
 */
@Data
@Builder
public class StorageObject {

    /**
     * Bucket 名称。
     */
    private String bucketName;

    /**
     * Object Key。
     */
    private String objectKey;

    /**
     * ETag。
     */
    private String etag;
}
