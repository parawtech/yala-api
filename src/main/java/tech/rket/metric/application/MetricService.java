package tech.rket.metric.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.rket.metric.infrastructure.persistence.MetricRepository;
import tech.rket.metric.query.MetricQuery;

import java.time.Instant;
import java.util.List;

import static tech.rket.metric.application.TimeRoundingUtils.*;

@Service
@RequiredArgsConstructor
public class MetricService {
    private final MetricRepository repository;

    public Double getOverallMetricSum(Long tenant, String name, Instant startTime, Instant endTime) {
        Instant startTimeFloor = roundDownToFiveMinutes(startTime);
        Instant endTimeCeil = roundUpToFiveMinutes(endTime);
        return repository.getOverallMetricSum(tenant, name, startTimeFloor, endTimeCeil);
    }

    public List<MetricQuery> getFragmentedMetricSums(Long tenant, String name, Instant startTime, Instant endTime) {
        Instant startTimeFloor = roundDownToFiveMinutes(startTime);
        Instant endTimeCeil = roundUpToFiveMinutes(endTime);
        return repository.getFragmentedMetricSums(tenant, name, startTimeFloor, endTimeCeil);
    }

    public Double getOverallMetricAverage(Long tenant, String name, Instant startTime, Instant endTime) {
        Instant startTimeFloor = roundDownToFiveMinutes(startTime);
        Instant endTimeCeil = roundUpToFiveMinutes(endTime);
        return repository.getOverallMetricAverage(tenant, name, startTimeFloor, endTimeCeil);
    }

    public List<MetricQuery> getFragmentedMetricAverages(Long tenant, String name, Instant startTime, Instant endTime) {
        Instant startTimeFloor = roundDownToFiveMinutes(startTime);
        Instant endTimeCeil = roundUpToFiveMinutes(endTime);
        return repository.getFragmentedMetricAverages(tenant, name, startTimeFloor, endTimeCeil);
    }
}
