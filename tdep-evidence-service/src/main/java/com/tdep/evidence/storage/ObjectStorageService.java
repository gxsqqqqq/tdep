package com.tdep.evidence.storage;

import java.io.InputStream;

/**
 * 对象存储服务。
 */
public interface ObjectStorageService {

    /**
     * 保存对象。
     *
     * @param bucketName  bucket
     * @param objectKey   object key
     * @param inputStream 输入流
     * @param size        文件大小
     * @param contentType 内容类型
     * @return 存储结果
     */
    StorageObject putObject(String bucketName, String objectKey, InputStream inputStream, long size, String contentType);

    /**
     * 读取对象。
     *
     * @param bucketName bucket
     * @param objectKey  object key
     * @return 对象输入流
     */
    InputStream getObject(String bucketName, String objectKey);

    /**
     * 生成预签名下载地址。
     *
     * @param bucketName bucket
     * @param objectKey  object key
     * @return 预签名 URL
     */
    String presignedGetUrl(String bucketName, String objectKey);
}
