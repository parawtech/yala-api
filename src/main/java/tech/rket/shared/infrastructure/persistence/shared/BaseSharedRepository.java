package tech.rket.shared.infrastructure.persistence.shared;

import tech.rket.shared.core.shared.HasId;
import tech.rket.shared.core.shared.SharedRepository;
import tech.rket.shared.infrastructure.persistence.PersistedObject;
import tech.rket.shared.infrastructure.persistence.mapper.PersistenceMapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public abstract class BaseSharedRepository<
        PERSISTED_OBJECT extends PersistedObject<PERSISTED_OBJECT_ID>,
        PERSISTED_OBJECT_ID,
        T extends HasId<ID>,
        ID>
        implements SharedRepository<T, ID> {

    protected abstract JpaRepository<PERSISTED_OBJECT, PERSISTED_OBJECT_ID> getRepository();

    protected abstract PersistenceMapper<PERSISTED_OBJECT, T> getMapper();

    protected abstract PERSISTED_OBJECT_ID convertID(ID val);

    @Override
    public Optional<T> findById(ID id) {
        return this.findEntityById(id)
                .map(getMapper()::convert);
    }

    @Override
    public boolean existsById(ID domainId) {
        return Optional.ofNullable(convertID(domainId))
                .map(getRepository()::existsById)
                .orElse(false);
    }

    protected Optional<PERSISTED_OBJECT> findEntityById(ID id) {
        return Optional.ofNullable(convertID(id))
                .flatMap(getRepository()::findById);
    }
}
