package com.tdep.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TDEP 认证授权服务启动类，负责用户登录、令牌签发和权限认证能力扩展。
 */
@MapperScan("com.tdep.auth.mapper")
@SpringBootApplication(scanBasePackages = "com.tdep")
public class TdepAuthServiceApplication {

    /**
     * 认证授权服务启动入口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(TdepAuthServiceApplication.class, args);
    }
}
