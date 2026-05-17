package com.tdep.document.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OssDocumentObjectStorageService implements DocumentObjectStorageService {

    private final OSS ossClient;

    private final DocumentStorageProperties storageProperties;

    @Override
    public DocumentStorageObject putObject(String bucketName, String objectKey, byte[] bytes, String contentType,
                                           Map<String, String> metadata) {
        return putObject(bucketName, objectKey, new ByteArrayInputStream(bytes), bytes.length, contentType, metadata);
    }

    @Override
    public DocumentStorageObject putObject(String bucketName, String objectKey, InputStream inputStream, long size,
                                           String contentType, Map<String, String> metadata) {
        try {
            ensureBucket(bucketName);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(size);
            if (contentType != null) {
                objectMetadata.setContentType(contentType);
            }
            if (metadata != null && !metadata.isEmpty()) {
                objectMetadata.setUserMetadata(safeMetadata(metadata));
            }
            PutObjectRequest putRequest = new PutObjectRequest(bucketName, objectKey, inputStream, objectMetadata);
            ossClient.putObject(putRequest);
            return statObject(bucketName, objectKey);
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "OSS 文件写入失败: " + ex.getMessage());
        }
    }

    @Override
    public InputStream getObject(String bucketName, String objectKey) {
        try {
            OSSObject ossObject = ossClient.getObject(bucketName, objectKey);
            return ossObject.getObjectContent();
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "OSS 文件读取失败: " + ex.getMessage());
        }
    }

    @Override
    public DocumentStorageObject statObject(String bucketName, String objectKey) {
        try {
            ObjectMetadata metadata = ossClient.getObjectMetadata(bucketName, objectKey);
            return DocumentStorageObject.builder()
                    .bucketName(bucketName)
                    .objectKey(objectKey)
                    .etag(metadata.getETag())
                    .size(metadata.getContentLength())
                    .contentType(metadata.getContentType())
                    .metadata(metadata.getUserMetadata() != null ? new HashMap<>(metadata.getUserMetadata()) : Map.of())
                    .build();
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "OSS 文件元数据读取失败: " + ex.getMessage());
        }
    }

    @Override
    public DocumentStorageObject copyObject(String sourceBucketName, String sourceObjectKey, String targetBucketName,
                                            String targetObjectKey, Map<String, String> metadata) {
        try {
            ensureBucket(targetBucketName);
            CopyObjectRequest copyRequest = new CopyObjectRequest(sourceBucketName, sourceObjectKey, targetBucketName, targetObjectKey);
            if (metadata != null && !metadata.isEmpty()) {
                ObjectMetadata newMetadata = new ObjectMetadata();
                newMetadata.setUserMetadata(safeMetadata(metadata));
                copyRequest.setNewObjectMetadata(newMetadata);
            }
            ossClient.copyObject(copyRequest);
            return statObject(targetBucketName, targetObjectKey);
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "OSS 文件复制失败: " + ex.getMessage());
        }
    }

    @Override
    public String presignedGetUrl(String bucketName, String objectKey) {
        try {
            Date expiration = new Date(System.currentTimeMillis() + storageProperties.getPresignedUrlSeconds() * 1000L);
            URL url = ossClient.generatePresignedUrl(bucketName, objectKey, expiration);
            return url.toString();
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "OSS 预签名地址生成失败: " + ex.getMessage());
        }
    }

    private void ensureBucket(String bucketName) {
        boolean exists = ossClient.doesBucketExist(bucketName);
        if (!exists) {
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
            ossClient.createBucket(createBucketRequest);
        }
    }

    private Map<String, String> safeMetadata(Map<String, String> metadata) {
        return metadata == null ? Map.of() : new HashMap<>(metadata);
    }
}
