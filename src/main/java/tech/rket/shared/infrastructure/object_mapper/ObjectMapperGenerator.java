package tech.rket.shared.infrastructure.object_mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;

@Configuration
@RequiredArgsConstructor
public class ObjectMapperGenerator {
    private static final ObjectMapper JSON_MAPPER;
    private final JacksonProperties jacksonProperties;
    @PostConstruct
    public void init() {
        jacksonProperties.getGenerator().forEach(JSON_MAPPER::configure);
        jacksonProperties.getMapper().forEach(JSON_MAPPER::configure);
        jacksonProperties.getParser().forEach(JSON_MAPPER::configure);
        jacksonProperties.getSerialization().forEach(JSON_MAPPER::configure);
        jacksonProperties.getDeserialization().forEach(JSON_MAPPER::configure);
        if (jacksonProperties.getDefaultLeniency() != null) {
            JSON_MAPPER.setDefaultLeniency(jacksonProperties.getDefaultLeniency());
        }
        if (jacksonProperties.getTimeZone() != null) {
            JSON_MAPPER.setTimeZone(jacksonProperties.getTimeZone());
        }

        if (jacksonProperties.getLocale() != null) {
            JSON_MAPPER.setLocale(jacksonProperties.getLocale());
        }
    }

    static {
        JSON_MAPPER = JsonMapper.builder().build()
                .registerModule(new JavaTimeModule())
                .setDateFormat(new StdDateFormat().withColonInTimeZone(true));
    }

    public static ObjectMapper jsonMapper() {
        return JSON_MAPPER;
    }

    @Bean("jsonObjectMapper")
    @Primary
    public ObjectMapper objectMapper() {
        return jsonMapper();
    }
}
