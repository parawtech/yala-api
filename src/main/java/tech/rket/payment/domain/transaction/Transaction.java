package tech.rket.payment.domain.transaction;

import tech.rket.payment.domain.owner.Owner;
import tech.rket.payment.domain.shared.IPG;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import tech.rket.shared.infrastructure.model.domain.JDEntity;
import tech.rket.shared.infrastructure.model.dto.JDAuditedEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;


@Entity
@Table(name = "payment_transaction")
@Data
@EqualsAndHashCode(callSuper = true)
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class Transaction extends JDEntity implements JDAuditedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Owner owner;
    @Column(name = "owner_reference")
    private String reference;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private ObjectNode additionalInfo;
    @Enumerated(EnumType.STRING)
    private IPG ipg;

    @Transient
    private LinkedHashMap<Number, Instant> auditRevisions;
}
