package com.medsync.common.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret = "change-me-in-production";
    private long expiration = 86400000;
    private long refreshExpiration = 604800000;
}
