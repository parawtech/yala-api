package tech.rket.shared.infrastructure.model.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.Instant;

@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public class JDEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "jdid-generator")
    @GenericGenerator(name = "jdid-generator", type = JdIdGenerator.class)
    protected BigInteger id;
    @CreatedDate
    @Column(name = "_created_date", updatable = false)
    protected Instant createdDate;
    @LastModifiedDate
    @CreatedDate
    @Column(name = "_updated_date")
    protected Instant updatedDate;
    @Version
    @Column(name = "_version")
    protected Integer version;
}
