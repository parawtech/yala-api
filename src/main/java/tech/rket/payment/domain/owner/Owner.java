package tech.rket.payment.domain.owner;

import tech.rket.payment.domain.shared.IPG;
import tech.rket.payment.domain.transaction.OwnerSelfConfig;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import tech.rket.shared.infrastructure.model.domain.IdentifiedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "payment_owner")
@Data
@EqualsAndHashCode(callSuper = true)
@EntityListeners(OwnerEntityListener.class)
public class Owner extends IdentifiedEntity {
    @Column(columnDefinition = "json")
    @Type(JsonType.class)
    private OwnerSelfConfig selfConfig;
    @Column(columnDefinition = "json")
    @Type(JsonType.class)
    private List<OwnerConfig> configs;


    public Optional<OwnerConfig> getOwnerConfig(IPG ipg) {
        return configs.stream().filter(oc -> oc.getIpg().equals(ipg)).findFirst();
    }
}
