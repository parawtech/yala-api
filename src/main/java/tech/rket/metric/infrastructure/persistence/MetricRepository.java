package tech.rket.metric.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.rket.metric.query.MetricQuery;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface MetricRepository extends JpaRepository<MetricEntity, Long> {

    Optional<MetricEntity> findByTenantAndNameAndTimeWindow(Long tenant, String name, Instant roundedTimeWindow);

    @Query("""
            SELECT new tech.rket.metric.query.MetricQuery(m.timeWindow, SUM(m.value))
            FROM MetricEntity m
            WHERE m.tenant = :tenant AND m.name = :name
              AND m.timeWindow BETWEEN :startTime AND :endTime
            GROUP BY m.timeWindow
            ORDER BY m.timeWindow
            """)
    List<MetricQuery> getFragmentedMetricSums(@Param("tenant") Long tenant, @Param("profile") String name, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime);

    @Query("""
            SELECT SUM(m.value)
            FROM MetricEntity m
            WHERE m.tenant = :tenant AND m.name = :name
              AND m.timeWindow BETWEEN :startTime AND :endTime
            """)
    Double getOverallMetricSum(@Param("tenant") Long tenant, @Param("profile") String name, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime);

    @Query("""
            SELECT new tech.rket.metric.query.MetricQuery(m.timeWindow, AVG(m.value))
            FROM MetricEntity m
            WHERE m.tenant = :tenant AND m.name = :name
              AND m.timeWindow BETWEEN :startTime AND :endTime
            GROUP BY m.timeWindow
            ORDER BY m.timeWindow
            """)
    List<MetricQuery> getFragmentedMetricAverages(@Param("tenant") Long tenant, @Param("profile") String name, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime);

    @Query("""
            SELECT AVG(m.value)
            FROM MetricEntity m
            WHERE m.tenant = :tenant AND m.name = :name
              AND m.timeWindow BETWEEN :startTime AND :endTime
            """)
    Double getOverallMetricAverage(@Param("tenant") Long tenant, @Param("profile") String name, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime);
}



