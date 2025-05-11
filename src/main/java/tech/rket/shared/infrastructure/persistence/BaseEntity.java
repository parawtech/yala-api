package tech.rket.shared.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
abstract public class BaseEntity {
    @CreatedDate
    @Column(name = "__cdt")
    protected Instant createdDate;
    @LastModifiedDate
    @Column(name = "__udt")
    protected Instant updatedDate;
    @Version
    @Column(name = "__ver")
    protected Integer version;
}
