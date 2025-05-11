
package tech.rket.shared.infrastructure.scheduling;

import lombok.Data;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@ConfigurationProperties(prefix = "shared.scheduling.redis")
@ConditionalOnProperty(value = "shared.scheduling.shedlock.lock-type", havingValue = "redis")
@ConditionalOnBean(ShedLockConfig.class)
@Data
public class ShedLockRedisLockProviderConfiguration {
    private String environment = "default";
    private String keyPrefix = "job-lock";

    @Bean
    public RedisLockProvider redisLockProvider(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockProvider.Builder(redisConnectionFactory)
                .environment(this.getEnvironment())
                .keyPrefix(this.getKeyPrefix())
                .build();
    }
}
