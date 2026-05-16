package com.tdep.gateway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关安全配置属性，用于维护 JWT 参数和路由白名单。
 */
@Data
@ConfigurationProperties(prefix = "tdep.gateway.security")
public class GatewaySecurityProperties {

    /**
     * 不需要网关 JWT 校验的路径。
     */
    private List<String> permitAllUrls = new ArrayList<>();

    /**
     * 签发方标识。
     */
    private String issuer = "tdep";

    /**
     * JWT 签名密钥，生产环境必须替换。
     */
    private String secret = "tdep-development-secret-key-please-change-in-production";

    /**
     * 请求头名称。
     */
    private String header = "Authorization";

    /**
     * 请求头令牌前缀。
     */
    private String tokenPrefix = "Bearer ";
}
