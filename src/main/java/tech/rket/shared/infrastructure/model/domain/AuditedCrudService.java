package tech.rket.shared.infrastructure.model.domain;

import tech.rket.shared.infrastructure.exception.rest.NotFoundException;
import tech.rket.shared.infrastructure.model.dto.JDAuditedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public abstract class AuditedCrudService<T extends JDEntity & JDAuditedEntity, D, R extends JpaRepository<T, BigInteger> & AuditedRepository<T>> extends CrudService<T, D, R> {
    @Transactional(readOnly = true)
    public Optional<T> find(BigInteger id, Integer version) {
        Optional<T> findById = getRepository().findById(id);
        if (version != null && (findById.isEmpty() || !findById.get().getVersion().equals(version))) {
            findById = getRepository().findWithVersion(classifiedId(id), version).map(this::remap);
        }
        return postLoad(findById);
    }


    @Transactional(readOnly = true)
    public Optional<T> find(BigInteger id, Instant instant) {
        if (instant == null) {
            return postLoad(getRepository().findById(id));
        } else {
            return postLoad(getRepository().findWithDate(classifiedId(id), instant).map(this::remap));
        }
    }

    @Transactional(readOnly = true)
    public Optional<T> find(BigInteger id, Integer version, Instant instant) {
        if (instant == null) {
            return find(id, version);
        } else {
            return find(id, instant);
        }
    }

    @Transactional(readOnly = true)
    public T getEntity(BigInteger id, Integer version, Instant instant) {
        return find(id, version, instant).orElseThrow(() -> notFoundException(id, version, instant));
    }

    @Transactional(readOnly = true)
    public T getEntity(BigInteger id, Integer version) {
        return find(id, version).orElseThrow(() -> notFoundException(id, version, null));
    }

    @Transactional(readOnly = true)
    public T getEntity(BigInteger id, Instant instant) {
        return find(id, instant).orElseThrow(() -> notFoundException(id, null, instant));
    }

    @Transactional(readOnly = true)
    public D get(BigInteger id, Integer version, Instant instant) {
        return map(getEntity(id, version, instant));
    }

    @Transactional(readOnly = true)
    public D get(BigInteger id, Integer version) {
        return map(getEntity(id, version));
    }

    @Transactional(readOnly = true)
    public D get(BigInteger id, Instant instant) {
        return map(getEntity(id, instant));
    }

    protected NotFoundException notFoundException(BigInteger id, Integer version, Instant instant) {
        return new NotFoundException(notFoundError(id, version, instant));
    }

    protected String notFoundError(BigInteger id, Integer version, Instant instant) {
        String idString = id.toString();
        if (version == null && instant == null) {
            return String.format("Entity with ID %s was not found.", idString);
        } else if (version == null) {
            return String.format("Entity with ID %s was not found at %s.", idString, instant.toString());
        } else if (instant == null) {
            return String.format("Entity with ID %s and version %d was not found.", idString, version);
        } else {
            return String.format("Entity with ID %s and version %d was not found at %s.", idString, version, instant.toString());
        }
    }

    protected abstract T classifiedId(BigInteger id);

    protected T remap(T value) {
        return value;
    }

    public List<Instant> getVersionsDate(BigInteger id) {
        return getRepository().findAllRevisionDate(classifiedId(id));
    }

    public Integer getLatestVersion(BigInteger id) {
        return getRepository().findLatestVersion(classifiedId(id)).orElse(-1);
    }
}
