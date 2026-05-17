package com.tdep.document.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OssDocumentConfig {

    private final DocumentStorageProperties storageProperties;

    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(
                storageProperties.getEndpoint(),
                storageProperties.getAccessKeyId(),
                storageProperties.getAccessKeySecret()
        );
    }
}
