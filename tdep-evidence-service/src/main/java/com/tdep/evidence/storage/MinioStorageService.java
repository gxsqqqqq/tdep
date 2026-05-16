package com.tdep.evidence.storage;

import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * MinIO 对象存储服务实现。
 */
@Service
@RequiredArgsConstructor
public class MinioStorageService implements ObjectStorageService {

    private final MinioClient minioClient;

    private final StorageProperties storageProperties;

    @Override
    public StorageObject putObject(String bucketName, String objectKey, InputStream inputStream, long size, String contentType) {
        try {
            ensureBucket(bucketName);
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .build());
            String etag = minioClient.statObject(StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build())
                    .etag();
            return StorageObject.builder()
                    .bucketName(bucketName)
                    .objectKey(objectKey)
                    .etag(etag)
                    .build();
        } catch (Exception exception) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "MinIO 文件写入失败：" + exception.getMessage());
        }
    }

    @Override
    public InputStream getObject(String bucketName, String objectKey) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .build());
        } catch (Exception exception) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "MinIO 文件读取失败：" + exception.getMessage());
        }
    }

    @Override
    public String presignedGetUrl(String bucketName, String objectKey) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectKey)
                    .expiry(storageProperties.getPresignedUrlSeconds())
                    .build());
        } catch (Exception exception) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "MinIO 预签名地址生成失败：" + exception.getMessage());
        }
    }

    /**
     * 确保 bucket 存在。
     *
     * @param bucketName bucket 名称
     * @throws Exception MinIO 异常
     */
    private void ensureBucket(String bucketName) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }
}
