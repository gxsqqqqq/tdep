package com.tdep.evidence;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TDEP 电子证据服务启动类，负责在线司法存证和电子证据管理能力扩展。
 */
@MapperScan("com.tdep.evidence.mapper")
@SpringBootApplication(scanBasePackages = "com.tdep")
public class TdepEvidenceServiceApplication {

    /**
     * 电子证据服务启动入口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(TdepEvidenceServiceApplication.class, args);
    }
}
