package tech.rket.metric.query;

import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface MetricQueryRepository {
    List<MetricQuery> getFragmentedMetricSums(@Param("tenant") Long tenant, @Param("profile") String name, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime);

    Double getOverallMetricSum(@Param("tenant") Long tenant, @Param("profile") String name, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime);

    List<MetricQuery> getFragmentedMetricAverages(@Param("tenant") Long tenant, @Param("profile") String name, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime);

    Double getOverallMetricAverage(@Param("tenant") Long tenant, @Param("profile") String name, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime);
}
