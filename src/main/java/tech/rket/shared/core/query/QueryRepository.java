package tech.rket.shared.core.query;

import tech.rket.shared.core.shared.SharedRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.Set;

public interface QueryRepository<QUERY_OBJECT_ID extends QueryObject<ID>, ID>
        extends SharedRepository<QUERY_OBJECT_ID, ID> {
    default Set<QUERY_OBJECT_ID> findAllByIds(Collection<ID> ids) {
        return findAllByIds(ids, Sort.unsorted());
    }

    Set<QUERY_OBJECT_ID> findAllByIds(Collection<ID> ids, Sort sort);

    Page<QUERY_OBJECT_ID> findAll(Pageable pageable);

    Set<QUERY_OBJECT_ID> findAll(Sort sort);

    default Set<QUERY_OBJECT_ID> findAll() {
        return findAll(Sort.unsorted());
    }

    long count();
}
