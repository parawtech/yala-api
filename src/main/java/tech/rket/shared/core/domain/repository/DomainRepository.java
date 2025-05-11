package tech.rket.shared.core.domain.repository;

import tech.rket.shared.core.domain.DomainObject;
import tech.rket.shared.core.shared.SharedRepository;

import java.util.Optional;

public interface DomainRepository<DOMAIN extends DomainObject.Entity.AggregateRoot<ID>, ID>
        extends
        SharedRepository<DOMAIN, ID> {
    void save(DOMAIN val);

    Optional<DOMAIN> findById(ID id);

    void deleteById(ID id);

    ID generateID();
}
