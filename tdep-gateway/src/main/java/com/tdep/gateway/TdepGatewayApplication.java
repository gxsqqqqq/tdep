package com.tdep.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * TDEP API 网关启动类，统一承载外部流量入口、路由转发和网关级 JWT 校验。
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class TdepGatewayApplication {

    /**
     * 网关服务启动入口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(TdepGatewayApplication.class, args);
    }
}
