package com.tdep.evidence.storage;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 客户端配置。
 */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class MinioConfig {

    /**
     * 创建 MinIO 客户端。
     *
     * @param properties 存储配置
     * @return MinIO 客户端
     */
    @Bean
    public MinioClient minioClient(StorageProperties properties) {
        return MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
    }
}
