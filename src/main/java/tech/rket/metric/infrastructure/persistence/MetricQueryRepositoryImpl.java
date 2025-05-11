package tech.rket.metric.infrastructure.persistence;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tech.rket.metric.query.MetricQuery;
import tech.rket.metric.query.MetricQueryRepository;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@Component
public class MetricQueryRepositoryImpl implements MetricQueryRepository {
    private MetricRepository metricRepository;

    @Override
    public List<MetricQuery> getFragmentedMetricSums(Long tenant, String name, Instant startTime, Instant endTime) {
        return metricRepository.getFragmentedMetricSums(tenant, name, startTime, endTime);
    }

    @Override
    public Double getOverallMetricSum(Long tenant, String name, Instant startTime, Instant endTime) {
        return metricRepository.getOverallMetricSum(tenant, name, startTime, endTime);
    }

    @Override
    public List<MetricQuery> getFragmentedMetricAverages(Long tenant, String name, Instant startTime, Instant endTime) {
        return metricRepository.getFragmentedMetricAverages(tenant, name, startTime, endTime);
    }

    @Override
    public Double getOverallMetricAverage(Long tenant, String name, Instant startTime, Instant endTime) {
        return metricRepository.getOverallMetricAverage(tenant, name, startTime, endTime);
    }
}
