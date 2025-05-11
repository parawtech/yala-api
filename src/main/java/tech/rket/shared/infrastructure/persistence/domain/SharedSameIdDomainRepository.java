package tech.rket.shared.infrastructure.persistence.domain;

import tech.rket.shared.core.domain.DomainObject;
import tech.rket.shared.infrastructure.persistence.PersistedObject;

public abstract class SharedSameIdDomainRepository<
        PERSISTED_ID extends PersistedObject<ID>,
        DOMAIN extends DomainObject.Entity.AggregateRoot<ID>,
        ID>
        extends SharedDomainRepository<PERSISTED_ID, ID, DOMAIN, ID> {
    @Override
    protected ID convertID(ID val) {
        return val;
    }
}