package tech.rket.metric.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tech.rket.metric.infrastructure.persistence.MetricEntity;
import tech.rket.metric.infrastructure.persistence.MetricRepository;
import tech.rket.metric.infrastructure.persistence.MetricType;
import tech.rket.shared.infrastructure.object_mapper.ObjectMapperGenerator;

import java.time.Instant;
import java.util.Iterator;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MetricsExporter {
    private final MeterRegistry meterRegistry;
    private final MetricRepository metricRepository;
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperGenerator.jsonMapper();
    @Value(("${metric.scheduling.export-metrics.enable}"))
    private boolean enableSchedule;

    @Scheduled(fixedDelay = 30_000)
    @SchedulerLock(name = "metric.exportMetrics", lockAtMostFor = "60s", lockAtLeastFor = "30s")
    public void exportTenantMetrics() {
        if (!enableSchedule) {
            return;
        }
        Instant roundedTimeWindow = truncateToFiveMinuteInterval(Instant.now());
        meterRegistry.getMeters().stream()
                .filter(e -> e.getId().getName().startsWith("rket."))
                .forEach(meter -> {
                    Meter.Id id = meter.getId();
                    String name = id.getName();
                    MetricType type = MetricType.valueOf(id.getType().name());
                    String tags;
                    try {
                        tags = OBJECT_MAPPER.writeValueAsString(id.getTags());
                    } catch (JsonProcessingException e) {
                        tags = null;
                    }
                    double value = 0.0;
                    Iterator<Measurement> measurements = meter.measure().iterator();
                    if (measurements.hasNext()) {
                        Measurement measurement = measurements.next();
                        value = measurement.getValue();
                    }
                    String tenantTagValue = id.getTag("tenant");
                    Long tenant = tenantTagValue == null || tenantTagValue.isBlank() ? null : Long.parseLong(tenantTagValue);
                    persist(name, tenant, tags, type, value, roundedTimeWindow);
                });
    }

    private void persist(String name, Long tenant, String tags, MetricType type, Double value, Instant roundedTimeWindow) {
        Optional<MetricEntity> existingMetric = metricRepository.findByTenantAndNameAndTimeWindow(tenant, name, roundedTimeWindow);
        try {
            if (existingMetric.isPresent()) {
                update(existingMetric.get(), value);
            } else {
                create(name, tenant, tags, type, value, roundedTimeWindow);
            }
        } catch (ConstraintViolationException constraintViolationException) {
            persist(name, tenant, tags, type, value, roundedTimeWindow);
        }
    }

    private void create(String name, Long tenant, String tags, MetricType type, Double value, Instant roundedTimeWindow) {
        MetricEntity metric = new MetricEntity();
        metric.set(name, type, value, tenant, tags, roundedTimeWindow);
        metricRepository.save(metric);
    }

    private void update(MetricEntity metricEntity, Double value) {
        metricEntity.update(metricEntity.getValue() + value);
        metricRepository.save(metricEntity);
    }

    private Instant truncateToFiveMinuteInterval(Instant instant) {
        long truncatedMillis = (instant.toEpochMilli() / 300000) * 300000;
        return Instant.ofEpochMilli(truncatedMillis);
    }
}