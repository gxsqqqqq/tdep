package com.tdep.document.storage;

import java.io.InputStream;
import java.util.Map;

public interface DocumentObjectStorageService {

    DocumentStorageObject putObject(String bucketName, String objectKey, byte[] bytes, String contentType,
                                    Map<String, String> metadata);

    DocumentStorageObject putObject(String bucketName, String objectKey, InputStream inputStream, long size,
                                    String contentType, Map<String, String> metadata);

    InputStream getObject(String bucketName, String objectKey);

    DocumentStorageObject statObject(String bucketName, String objectKey);

    DocumentStorageObject copyObject(String sourceBucketName, String sourceObjectKey, String targetBucketName,
                                     String targetObjectKey, Map<String, String> metadata);

    String presignedGetUrl(String bucketName, String objectKey);
}
