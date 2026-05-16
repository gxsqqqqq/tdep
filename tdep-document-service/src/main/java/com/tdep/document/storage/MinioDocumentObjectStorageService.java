package com.tdep.document.storage;

import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import io.minio.BucketExistsArgs;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketVersioningArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.http.Method;
import io.minio.messages.VersioningConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MinioDocumentObjectStorageService implements DocumentObjectStorageService {

    private final MinioClient minioClient;

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
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .userMetadata(safeMetadata(metadata))
                    .build());
            return statObject(bucketName, objectKey);
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "MinIO 文件写入失败: " + ex.getMessage());
        }
    }

    @Override
    public InputStream getObject(String bucketName, String objectKey) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .build());
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "MinIO 文件读取失败: " + ex.getMessage());
        }
    }

    @Override
    public DocumentStorageObject statObject(String bucketName, String objectKey) {
        try {
            StatObjectResponse response = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .build());
            return DocumentStorageObject.builder()
                    .bucketName(bucketName)
                    .objectKey(objectKey)
                    .etag(response.etag())
                    .versionId(response.versionId())
                    .size(response.size())
                    .contentType(response.contentType())
                    .metadata(response.userMetadata())
                    .build();
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "MinIO 文件元数据读取失败: " + ex.getMessage());
        }
    }

    @Override
    public DocumentStorageObject copyObject(String sourceBucketName, String sourceObjectKey, String targetBucketName,
                                            String targetObjectKey, Map<String, String> metadata) {
        try {
            ensureBucket(targetBucketName);
            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(targetBucketName)
                    .object(targetObjectKey)
                    .source(CopySource.builder()
                            .bucket(sourceBucketName)
                            .object(sourceObjectKey)
                            .build())
                    .userMetadata(safeMetadata(metadata))
                    .build());
            return statObject(targetBucketName, targetObjectKey);
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "MinIO 文件复制失败: " + ex.getMessage());
        }
    }

    @Override
    public String presignedGetUrl(String bucketName, String objectKey) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .method(Method.GET)
                    .expiry(storageProperties.getPresignedUrlSeconds())
                    .build());
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "MinIO 预签名地址生成失败: " + ex.getMessage());
        }
    }

    private void ensureBucket(String bucketName) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            minioClient.setBucketVersioning(SetBucketVersioningArgs.builder()
                    .bucket(bucketName)
                    .config(new VersioningConfiguration(VersioningConfiguration.Status.ENABLED, false))
                    .build());
        }
    }

    private Map<String, String> safeMetadata(Map<String, String> metadata) {
        return metadata == null ? Map.of() : new HashMap<>(metadata);
    }
}
