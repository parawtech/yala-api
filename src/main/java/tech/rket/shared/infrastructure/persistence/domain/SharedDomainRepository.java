package tech.rket.shared.infrastructure.persistence.domain;

import tech.rket.shared.core.domain.DomainObject;
import tech.rket.shared.core.domain.repository.DomainRepository;
import tech.rket.shared.infrastructure.persistence.PersistedObject;
import tech.rket.shared.infrastructure.persistence.mapper.DomainPersistenceMapper;
import tech.rket.shared.infrastructure.persistence.shared.BaseSharedRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public abstract class SharedDomainRepository<PERSISTED_OBJECT extends PersistedObject<PERSISTED_OBJECT_ID>, PERSISTED_OBJECT_ID, DOMAIN extends DomainObject.Entity.AggregateRoot<ID>, ID> extends BaseSharedRepository<PERSISTED_OBJECT, PERSISTED_OBJECT_ID, DOMAIN, ID> implements DomainRepository<DOMAIN, ID> {
    protected abstract DomainPersistenceMapper<PERSISTED_OBJECT, DOMAIN> getMapper();

    @Override
    @Transactional
    public void save(DOMAIN val) {
        Optional<PERSISTED_OBJECT> persistedObjectOptional = findEntityById(val.getId());
        PERSISTED_OBJECT persistedObject;
        if (persistedObjectOptional.isEmpty()) {
            persistedObject = create(val);
        } else {
            persistedObject = update(persistedObjectOptional.get(), val);
        }

        persist(persistedObject);
    }

    protected void persist(PERSISTED_OBJECT persistedObject) {
        this.getRepository().save(persistedObject);
    }

    protected PERSISTED_OBJECT create(DOMAIN value) {
        return getMapper().create(value);

    }

    protected PERSISTED_OBJECT update(PERSISTED_OBJECT e, DOMAIN val) {
        return getMapper().update(e, val);
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        this.getRepository().deleteById(convertID(id));
    }
}
