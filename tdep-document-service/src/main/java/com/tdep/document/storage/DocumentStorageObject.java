package com.tdep.document.storage;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DocumentStorageObject {

    private String bucketName;

    private String objectKey;

    private String etag;

    private String versionId;

    private Long size;

    private String contentType;

    private Map<String, String> metadata;
}
