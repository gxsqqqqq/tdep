package com.tdep.document;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.tdep.document.mapper")
@SpringBootApplication(scanBasePackages = "com.tdep")
@ConfigurationPropertiesScan(basePackages = "com.tdep.document")
@EnableScheduling
public class TdepDocumentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TdepDocumentServiceApplication.class, args);
    }
}
