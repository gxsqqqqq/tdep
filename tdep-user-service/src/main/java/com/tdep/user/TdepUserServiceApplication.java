package com.tdep.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TDEP 用户服务启动类，负责用户、组织、角色和权限数据能力扩展。
 */
@MapperScan("com.tdep.user.mapper")
@SpringBootApplication(scanBasePackages = "com.tdep")
public class TdepUserServiceApplication {

    /**
     * 用户服务启动入口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(TdepUserServiceApplication.class, args);
    }
}
