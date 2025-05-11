package tech.rket.shared.core.domain.event;

import tech.rket.shared.core.domain.DomainObject;

import java.util.List;

public interface DomainEventStore<E extends DomainObject.Entity.AggregateRoot<ID>, ID> {
    void store(E agreegate);

    List<DomainEvent<ID>> findById(ID id);
}
