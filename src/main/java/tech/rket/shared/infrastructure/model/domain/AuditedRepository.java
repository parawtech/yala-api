package tech.rket.shared.infrastructure.model.domain;


import jakarta.validation.constraints.NotNull;
import tech.rket.shared.infrastructure.model.dto.JDAuditedEntity;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AuditedRepository<T extends JDEntity & JDAuditedEntity> {
    Optional<T> findWithNearestVersion(@NotNull T entity, @NotNull Integer version);

    Optional<T> findWithNearestRevision(@NotNull T entity, @NotNull Number revision);

    Optional<T> findWithVersion(@NotNull T entity, @NotNull Integer version);

    Optional<T> findWithRevision(@NotNull T entity, @NotNull Number revision);

    Optional<T> findWithDate(@NotNull T entity, @NotNull Instant instant);

    List<Number> findAllRevisions(@NotNull T entity);

    Boolean hasVersion(@NotNull T entity, @NotNull Integer version);

    Boolean hasRevision(@NotNull T entity, @NotNull Number revision);

    Optional<Number> findRevision(@NotNull T entity, @NotNull Integer version);

    Optional<Integer> findVersion(@NotNull T entity, @NotNull Number version);

    Optional<Integer> findLatestVersion(@NotNull T entity);

    Optional<Number> findLatestRevision(@NotNull T entity);

    Optional<Integer> findNextVersion(@NotNull T entity, @NotNull Integer version);

    Optional<Integer> findPreviousVersion(@NotNull T entity, @NotNull Integer version);

    Optional<Number> findNextRevision(@NotNull T entity, @NotNull Number revision);

    Optional<Number> findPreviousRevision(@NotNull T entity, @NotNull Number revision);

    Optional<Integer> findNextVersion(@NotNull T entity);

    Optional<Integer> findPreviousVersion(@NotNull T entity);

    Optional<Number> findNextRevision(@NotNull T entity);

    Optional<Number> findPreviousRevision(@NotNull T entity);

    Optional<T> findNext(@NotNull T entity);

    Optional<T> findPrevious(@NotNull T entity);

    Optional<Integer> findVersion(@NotNull T entity, @NotNull Instant instant);

    Optional<Number> findRevision(@NotNull T entity, @NotNull Instant instant);

    Optional<Instant> findVersionedAt(@NotNull T entity, @NotNull Integer version);

    Optional<Instant> findRevisionedAt(@NotNull T entity, @NotNull Number revision);

    Optional<Integer> findNearestVersion(@NotNull T entity, @NotNull Integer version);

    Optional<Number> findNearestRevision(@NotNull T entity, @NotNull Number revision);

    Map<Number, Instant> findAllVersionDate(T entity);

    List<Instant> findAllRevisionDate(T entity);
}
