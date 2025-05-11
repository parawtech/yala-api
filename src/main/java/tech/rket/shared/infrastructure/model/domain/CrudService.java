package tech.rket.shared.infrastructure.model.domain;

import tech.rket.shared.infrastructure.exception.rest.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class CrudService<T extends JDEntity, D, R extends JpaRepository<T, BigInteger>> {
    protected abstract R getRepository();

    @Transactional(readOnly = true)
    public T getEntity(BigInteger id) {
        return postLoad(getRepository().findById(id)).orElseThrow(() -> notFoundException(id));
    }

    protected Optional<T> postLoad(Optional<T> optional) {
        return optional;
    }

    @Transactional(readOnly = true)
    public T save(T entity) {
        return getRepository().save(entity);
    }

    @Transactional(readOnly = true)
    public D get(BigInteger id) {
        return map(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Page<D> getAll(Pageable pageable) {
        return getRepository().findAll(pageable).map(this::map);
    }

    @Transactional(readOnly = true)
    public List<D> get(Collection<BigInteger> ids) {
        return getRepository().findAllById(ids).stream().map(this::map).toList();
    }

    protected abstract T map(D d);

    public abstract D map(T d);

    protected NotFoundException notFoundException(BigInteger id) {
        return new NotFoundException(notFoundError(id));
    }

    protected String notFoundError(BigInteger id) {
        return String.format("Entity with ID %s was not found.", id.toString());
    }

}
