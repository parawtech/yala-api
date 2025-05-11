package tech.rket.shared.infrastructure.restfilter;

import tech.rket.shared.infrastructure.restfilter.spring.filter.QueryService;
import tech.rket.shared.infrastructure.restfilter.spring.filter.QueryServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestFilterBean {
    @Bean
    public QueryService<?> queryService() {
        return new QueryServiceImpl<>();
    }
}
