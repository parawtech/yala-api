package tech.rket.shared.infrastructure.persistence.query;

import tech.rket.shared.core.query.QueryObject;
import tech.rket.shared.core.query.QueryRepository;
import tech.rket.shared.infrastructure.persistence.PersistedObject;
import tech.rket.shared.infrastructure.persistence.shared.BaseSharedRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class SharedQueryRepository<
        PERSISTED_OBJECT extends PersistedObject<PERSISTED_OBJECT_ID>,
        PERSISTED_OBJECT_ID,
        Q extends QueryObject<ID>,
        ID>
        extends BaseSharedRepository<PERSISTED_OBJECT, PERSISTED_OBJECT_ID, Q, ID>
        implements QueryRepository<Q, ID> {

    @Override
    @Transactional(readOnly = true)
    public Set<Q> findAllByIds(Collection<ID> ids, Sort sort) {
        return getRepository().findAll(sort).stream()
                .map(getMapper()::convert)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Q> findAll(Pageable pageable) {
        return getRepository()
                .findAll(pageable)
                .map(getMapper()::convert);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Q> findAll(Sort sort) {
        return getRepository()
                .findAll(sort).stream()
                .map(getMapper()::convert)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return getRepository()
                .count();
    }
}
