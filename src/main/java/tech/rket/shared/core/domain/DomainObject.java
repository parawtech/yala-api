package tech.rket.shared.core.domain;

import tech.rket.shared.core.domain.event.DomainEvent;
import tech.rket.shared.core.shared.HasId;

import java.util.Set;

public interface DomainObject {
    interface ValueObject extends DomainObject {
    }

    interface Entity<ID> extends DomainObject, HasId<ID> {
        interface AggregateRoot<ID> extends Entity<ID> {
            Set<DomainEvent<ID>> getEvents();
        }

        interface EntityRecord<ID> extends Entity<ID>, Record<ID> {
        }
    }
}
