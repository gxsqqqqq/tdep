package com.tdep.casecenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TDEP 案件服务启动类，负责案件管理和法律文书能力扩展。
 */
@MapperScan("com.tdep.casecenter.mapper")
@SpringBootApplication(scanBasePackages = "com.tdep")
public class TdepCaseServiceApplication {

    /**
     * 案件服务启动入口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(TdepCaseServiceApplication.class, args);
    }
}
