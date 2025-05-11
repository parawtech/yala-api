package tech.rket.payment.domain.attempt;

import tech.rket.payment.domain.shared.JDAdditionInfo;
import tech.rket.payment.domain.transaction.Transaction;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import tech.rket.shared.infrastructure.model.domain.JDEntity;
import tech.rket.shared.infrastructure.model.dto.JDAuditedEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import java.time.Instant;
import java.util.LinkedHashMap;


@Entity
@Table(name = "payment_attempt")
@Data
@EqualsAndHashCode(callSuper = true)
@Audited
public class Attempt extends JDEntity implements JDAuditedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Transaction transaction;
    private Long reference;
    @Enumerated(EnumType.STRING)
    private AttemptStatus status;
    @Enumerated(EnumType.STRING)
    public AttemptNotifyStatus notifyStatus = AttemptNotifyStatus.CURRENTLY_NO_NEED_TO_NOTIFY;
    private String description;
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private JDAdditionInfo additionalInfo = new JDAdditionInfo();
    @Transient
    private LinkedHashMap<Number, Instant> auditRevisions;
}
