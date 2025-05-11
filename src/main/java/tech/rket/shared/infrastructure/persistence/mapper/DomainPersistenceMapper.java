package tech.rket.shared.infrastructure.persistence.mapper;

import tech.rket.shared.core.domain.DomainObject;
import tech.rket.shared.infrastructure.persistence.PersistedObject;

public interface DomainPersistenceMapper<PERSISTED_OBJECT extends PersistedObject<?>, D extends DomainObject.Entity<?>>
        extends PersistenceMapper<PERSISTED_OBJECT, D> {
    PERSISTED_OBJECT convert(D t);

    default PERSISTED_OBJECT create(D t) {
        return convert(t);
    }

    PERSISTED_OBJECT update(PERSISTED_OBJECT persistedObject, D t);
}
