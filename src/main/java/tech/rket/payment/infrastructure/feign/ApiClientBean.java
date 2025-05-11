package tech.rket.payment.infrastructure.feign;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.Builder;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ApiClientBean {
    private static final Map<BeanKey, Object> map = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T apiClient(Class<T> tClass, String url) {
        return (T) map.computeIfAbsent(BeanKey.builder().tClass(tClass).url(url).build(),
                k -> Feign.builder()
                        .encoder(new JacksonEncoder())
                        .decoder(new JacksonDecoder())
                        .target(k.getTClass(), k.getUrl()));
    }

    @Data
    @Builder
    private static class BeanKey {
        private final Class<?> tClass;
        private final String url;
    }

}
