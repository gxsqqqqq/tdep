package com.tdep.ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TDEP AI 法律助手服务启动类，负责智能问答、文书辅助和法律检索能力扩展。
 */
@MapperScan("com.tdep.ai.mapper")
@SpringBootApplication(scanBasePackages = "com.tdep")
public class TdepAiServiceApplication {

    /**
     * AI 法律助手服务启动入口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(TdepAiServiceApplication.class, args);
    }
}
