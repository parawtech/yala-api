package tech.rket.metric.infrastructure.persistence;

public enum MetricType {
    COUNTER,
    GAUGE,
    TIMER,
    DISTRIBUTION_SUMMARY,
    LONG_TASK_TIMER,
    HISTOGRAM,
    PERCENTILE
}
