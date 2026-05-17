package com.tdep.evidence.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class OssStorageService implements ObjectStorageService {

    private final OSS ossClient;

    private final StorageProperties storageProperties;

    @Override
    public StorageObject putObject(String bucketName, String objectKey, InputStream inputStream, long size, String contentType) {
        try {
            ensureBucket(bucketName);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(size);
            if (contentType != null) {
                objectMetadata.setContentType(contentType);
            }
            PutObjectRequest putRequest = new PutObjectRequest(bucketName, objectKey, inputStream, objectMetadata);
            PutObjectResult result = ossClient.putObject(putRequest);
            return StorageObject.builder()
                    .bucketName(bucketName)
                    .objectKey(objectKey)
                    .etag(result.getETag())
                    .build();
        } catch (Exception exception) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "OSS 文件写入失败：" + exception.getMessage());
        }
    }

    @Override
    public InputStream getObject(String bucketName, String objectKey) {
        try {
            OSSObject ossObject = ossClient.getObject(bucketName, objectKey);
            return ossObject.getObjectContent();
        } catch (Exception exception) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "OSS 文件读取失败：" + exception.getMessage());
        }
    }

    @Override
    public String presignedGetUrl(String bucketName, String objectKey) {
        try {
            Date expiration = new Date(System.currentTimeMillis() + storageProperties.getPresignedUrlSeconds() * 1000L);
            URL url = ossClient.generatePresignedUrl(bucketName, objectKey, expiration);
            return url.toString();
        } catch (Exception exception) {
            throw new BusinessException(ResultCode.SERVICE_UNAVAILABLE, "OSS 预签名地址生成失败：" + exception.getMessage());
        }
    }

    private void ensureBucket(String bucketName) {
        boolean exists = ossClient.doesBucketExist(bucketName);
        if (!exists) {
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
            ossClient.createBucket(createBucketRequest);
        }
    }
}
