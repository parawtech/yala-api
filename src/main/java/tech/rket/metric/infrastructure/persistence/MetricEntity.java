package tech.rket.metric.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import tech.rket.shared.infrastructure.model.id.JID;
import tech.rket.shared.infrastructure.persistence.BaseEntity;

import java.time.Instant;

@Getter
@Entity
@Table(name = "metrics")
public class MetricEntity extends BaseEntity {
    @Id
    @JID
    private Long id;
    @Column(name = "metric_name")
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "metric_type")
    private MetricType type;
    private Double value;
    private Long tenant;
    private String tags;
    private Instant timeWindow;

    public void set(String name, MetricType type, Double value, Long tenant, String tags, Instant timewindow) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.tenant = tenant;
        this.tags = tags;
        this.timeWindow = timewindow;
    }

    public void update(double value) {
        this.value += value;
    }
}
