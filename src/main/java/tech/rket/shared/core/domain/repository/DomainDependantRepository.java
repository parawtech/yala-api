package tech.rket.shared.core.domain.repository;

import java.io.InputStream;
import java.util.Optional;

public interface DomainDependantRepository<ID, T> {
    void save(ID id, InputStream inputStream);

    Optional<T> findById(ID id);

    boolean has(ID id);

    void deleteById(ID id);
}
