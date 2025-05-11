package tech.rket.metric.query;

import java.time.Instant;

public record MetricQuery(Instant timestamp, Double value) {
}