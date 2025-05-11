package tech.rket.shared.core.shared;

import java.util.Optional;

public interface SharedRepository<T extends HasId<ID>, ID> {
    Optional<T> findById(ID id);

    default T getById(ID id) {
        return findById(id).orElse(null);
    }

    boolean existsById(ID id);
}
