package tech.rket.metric.infrastructure;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.rket.metric.infrastructure.aspect.DefaultValueResolver;
import tech.rket.metric.infrastructure.aspect.RketCountedAspect;
import tech.rket.metric.infrastructure.aspect.RketCountedMeterTagAnnotationHandler;
import tech.rket.metric.infrastructure.aspect.SpelValueExpressionResolver;
import tech.rket.shared.infrastructure.auth.UserLoginInfo;


@Configuration
@RequiredArgsConstructor
public class MetricsConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCustomizer() {
        return registry -> registry.config().meterFilter(new MeterFilter() {
            @NotNull
            @Override
            public Meter.Id map(@NotNull Meter.Id id) {
                if (!id.getName().startsWith("paraw.")) {
                    return id;
                }
                String tenant = String.valueOf(UserLoginInfo.findCurrent().map(UserLoginInfo::tenantId).orElse(null));
                if (tenant != null) {
                    return id.withTag(Tag.of("tenant", tenant));
                } else {
                    return id;
                }
            }
        });
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        TimedAspect timedAspect = new TimedAspect(registry);
        timedAspect.setMeterTagAnnotationHandler(new io.micrometer.core.aop.MeterTagAnnotationHandler(
                resolverClass -> new DefaultValueResolver(),
                expressionResolverClass -> new SpelValueExpressionResolver()
        ));
        return timedAspect;
    }

    @Bean
    public RketCountedAspect rketCountedAspect(MeterRegistry registry) {
        RketCountedAspect rketCountedAspect = new RketCountedAspect(registry);
        rketCountedAspect.setMeterTagAnnotationHandler(new RketCountedMeterTagAnnotationHandler(
                resolverClass -> new DefaultValueResolver(),
                expressionResolverClass -> new SpelValueExpressionResolver()
        ));
        return rketCountedAspect;
    }
}
