package tech.rket.shared.core.domain.event;

import tech.rket.shared.core.domain.DomainObject;

public interface DomainEventPublisher<E extends DomainObject.Entity.AggregateRoot<ID>, ID> {
    void publish(E agreegate);
}
