package com.tdep.common.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全配置属性，统一承载 JWT、白名单和跨域等安全参数。
 */
@Data
@ConfigurationProperties(prefix = "tdep.security")
public class SecurityProperties {

    /**
     * JWT 配置。
     */
    private Jwt jwt = new Jwt();

    /**
     * 不需要认证即可访问的接口白名单。
     */
    private List<String> permitAllUrls = new ArrayList<>();

    /**
     * JWT 参数配置。
     */
    @Data
    public static class Jwt {

        /**
         * 签发方标识。
         */
        private String issuer = "tdep";

        /**
         * 签名密钥，生产环境必须通过环境变量或配置中心覆盖。
         */
        private String secret = "tdep-development-secret-key-please-change-in-production";

        /**
         * 访问令牌有效期，单位秒。
         */
        private long accessTokenSeconds = 7200L;

        /**
         * 请求头名称。
         */
        private String header = "Authorization";

        /**
         * 请求头令牌前缀。
         */
        private String tokenPrefix = "Bearer ";
    }
}
