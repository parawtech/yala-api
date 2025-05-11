package tech.rket.auth.application.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "oauth")
@Data
public class TokenConfig {
    private String secretKey;
    private long accessTokenExpireSeconds;
    private long refreshTokenExpireSeconds;
}
