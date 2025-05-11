package tech.rket.shared.infrastructure.js;

import lombok.Data;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "shared.js.pool")
@Data
public class JsExecutorPoolConfig {
    private int maxTotal;
    private int minIdle;
    private int maxIdle;
    private boolean blockWhenExhausted;
    private boolean testOnBorrow;
    private boolean testOnReturn;

    @Bean
    public ObjectPool<NashronJsExecutor> jsExecutorPool() {
        GenericObjectPoolConfig<NashronJsExecutor> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(maxTotal);
        config.setMinIdle(minIdle);
        config.setMaxIdle(maxIdle);
        config.setBlockWhenExhausted(blockWhenExhausted);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setJmxEnabled(false);

        return new GenericObjectPool<>(new LazyExecutorFactory(), config);
    }
}
