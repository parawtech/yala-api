package tech.rket.shared.infrastructure.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

@Audited
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
public class NamedEntity extends JDEntity {
    @Column(name = "_name")
    protected String name;
    @Column(name = "_description")
    protected String description;
}
