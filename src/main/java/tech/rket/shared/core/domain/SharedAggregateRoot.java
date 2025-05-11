package tech.rket.shared.core.domain;

import tech.rket.shared.core.domain.event.DomainEvent;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class SharedAggregateRoot<ID> implements DomainObject.Entity.AggregateRoot<ID> {
    private final Set<DomainEvent<ID>> events = new LinkedHashSet<>();

    @Override
    public Set<DomainEvent<ID>> getEvents() {
        return Collections.unmodifiableSet(events);
    }

    protected void registerEvent(DomainEvent<ID> event) {
        events.add(event);
    }
}
