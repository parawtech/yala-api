package tech.rket.shared.infrastructure.model.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.exception.AuditException;
import org.hibernate.envers.internal.reader.AuditReaderImpl;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditId;
import org.hibernate.envers.query.criteria.AuditProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import tech.rket.shared.infrastructure.model.dto.JDAuditedEntity;
import tech.rket.shared.infrastructure.object_mapper.ObjectMapperGenerator;

import java.math.BigInteger;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Repository("AuditedRepositoryImpl-marketing")
@RequiredArgsConstructor
@Primary
public class AuditedRepositoryImpl<T extends JDEntity & JDAuditedEntity> implements AuditedRepository<T> {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperGenerator.jsonMapper();
    private final EntityManager entityManager;
    private static ThreadLocal<AuditReaderImpl> auditReader;

    private static void ensureAuditReader(EntityManager entityManager) {
        if (auditReader == null) {
            auditReader = new ThreadLocal<>();
        }
        if (auditReader.get() == null || !auditReader.get().getSession().isOpen()) {
            auditReader.remove();
            Session session = (Session) entityManager.getDelegate();
            try {
                AuditReaderImpl ari = (AuditReaderImpl) AuditReaderFactory.get(session);
                auditReader.set(ari);
            } catch (AuditException ae) {
                throw new IllegalStateException("Audit Reader not initialized", ae);
            }
        }
    }

    private AuditReader auditReader() {
        ensureAuditReader(entityManager);
        return auditReader.get();
    }

    T get(@NotNull T entity, @NotNull Number rev) {
        Class<T> tClass = (Class<T>) entity.getClass();
        T object = auditReader().find(tClass, entity.getId(), rev);
        if (object == null) {
            return null;
        }
        return reboot(entity, OBJECT_MAPPER.convertValue(object, tClass));
    }

    @Override
    public Optional<T> findWithNearestVersion(@NotNull T entity, @NotNull Integer version) {
        AtomicInteger possibleVersion = new AtomicInteger(0);
        return findNearestVersion(entity, version)
                .flatMap(ver -> {
                    possibleVersion.set(ver);
                    return findRevision(entity, ver);
                })
                .map(rev -> get(entity, rev))
                .map(e -> {
                    e.setVersion(possibleVersion.get());
                    return e;
                });
    }

    @Override
    public Optional<T> findWithNearestRevision(@NotNull T entity, @NotNull Number revision) {
        return findNearestRevision(entity, revision).map(rev -> get(entity, rev));
    }

    @Override
    public Optional<T> findWithVersion(@NotNull T entity, @NotNull Integer version) {
        return findRevision(entity, version)
                .map(rev -> get(entity, rev))
                .map(e -> {
                    e.setVersion(version);
                    return e;
                });
    }

    @Override
    public Optional<T> findWithRevision(@NotNull T entity, @NotNull Number revision) {
        return Optional.ofNullable(get(entity, revision));
    }

    @Override
    public Optional<T> findWithDate(@NotNull T entity, @NotNull Instant instant) {
        AtomicReference<Number> possibleRevision = new AtomicReference<>(null);
        return findRevision(entity, instant)
                .map(rev -> {
                    possibleRevision.set(rev);
                    return get(entity, rev);
                })
                .map(e -> {
                    findVersion(e, possibleRevision.get()).ifPresent(e::setVersion);
                    return e;
                });
    }

    @Override
    public List<Number> findAllRevisions(@NotNull T entity) {
        boot(entity);
        return new LinkedList<>(entity.getAuditRevisions().keySet());
    }

    private void boot(T entity) {
        if (entity.getAuditRevisions() == null) {
            entity.setAuditRevisions(findAllRevisionDates(entity));
        }
    }

    private T reboot(T entityContains, T entityForAddition) {
        if (entityForAddition.getAuditRevisions() == null) {
            entityForAddition.setAuditRevisions(entityContains.getAuditRevisions());
        }
        return entityForAddition;
    }

    @Override
    public Boolean hasVersion(@NotNull T entity, @NotNull Integer version) {
        return findNearestVersion(entity, version).filter(ver -> Objects.equals(ver, version)).isPresent();
    }

    @Override
    public Boolean hasRevision(@NotNull T entity, @NotNull Number revision) {
        return findAllRevisions(entity).contains(revision);
    }

    @Override
    public Optional<Number> findRevision(@NotNull T entity, @NotNull Integer version) {
        List<Number> list = findAllRevisions(entity);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return list.stream()
                .skip(version)
                .findFirst();
    }

    @Override
    public Optional<Integer> findVersion(@NotNull T entity, @NotNull Number version) {
        int indexOf = findAllRevisions(entity).indexOf(version);
        if (indexOf < 0) {
            return Optional.empty();
        } else {
            return Optional.of(indexOf);
        }
    }

    @Override
    public Optional<Integer> findLatestVersion(@NotNull T entity) {
        return findAllRevisionsOptional(entity).map(List::size).map(e -> e - 1);
    }

    @Override
    public Optional<Number> findLatestRevision(@NotNull T entity) {
        return findAllRevisionsOptional(entity).map(e -> e.get(e.size() - 1));
    }

    @Override
    public Optional<Integer> findNextVersion(@NotNull T entity, @NotNull Integer version) {
        return findLatestVersion(entity)
                .map(lv -> {
                    if (version < 0) {
                        return null;
                    } else if (version >= lv) {
                        return null;
                    } else {
                        return version + 1;
                    }
                });
    }

    @Override
    public Optional<Integer> findPreviousVersion(@NotNull T entity, @NotNull Integer version) {
        if (version <= 0) {
            return Optional.empty();
        }
        return findLatestVersion(entity)
                .map(lv -> {
                    if (version > lv + 1) {
                        return null;
                    } else {
                        return version - 1;
                    }
                });
    }

    @Override
    public Optional<Number> findNextRevision(@NotNull T entity, @NotNull Number revision) {
        List<Number> allVersions = findAllRevisions(entity);
        int index = allVersions.indexOf(revision);
        Number previousVersion = index >= 0 && index < allVersions.size() - 1 ? allVersions.get(index + 1) : null;
        return Optional.ofNullable(previousVersion);
    }

    @Override
    public Optional<Number> findPreviousRevision(@NotNull T entity, @NotNull Number revision) {
        List<Number> allVersions = findAllRevisions(entity);
        int index = allVersions.indexOf(revision);
        Number previousVersion = index > 0 ? allVersions.get(index - 1) : null;
        return Optional.ofNullable(previousVersion);
    }

    @Override
    public Optional<Integer> findNextVersion(@NotNull T entity) {
        Objects.requireNonNull(entity.getVersion(), "version is null");
        return findNextVersion(entity, entity.getVersion());
    }

    @Override
    public Optional<Integer> findPreviousVersion(@NotNull T entity) {
        Objects.requireNonNull(entity.getVersion(), "version is null");
        return findPreviousVersion(entity, entity.getVersion());
    }

    @Override
    public Optional<Number> findNextRevision(@NotNull T entity) {
        Objects.requireNonNull(entity.getVersion(), "version is null");
        return findLatestRevision(entity).flatMap(revision -> findNextRevision(entity, revision));
    }

    @Override
    public Optional<Number> findPreviousRevision(@NotNull T entity) {
        Objects.requireNonNull(entity.getVersion(), "version is null");
        return findLatestRevision(entity).flatMap(revision -> findPreviousRevision(entity, revision));
    }

    @Override
    public Optional<T> findNext(@NotNull T entity) {
        return findNextVersion(entity).flatMap(version -> findWithVersion(entity, version));
    }

    @Override
    public Optional<T> findPrevious(@NotNull T entity) {
        return findPreviousVersion(entity).flatMap(version -> findWithVersion(entity, version));
    }

    @Override
    public Optional<Integer> findVersion(@NotNull T entity, @NotNull Instant instant) {
        return findRevision(entity, instant).flatMap(revision -> findVersion(entity, revision));
    }

    @Override
    public Optional<Number> findRevision(@NotNull T entity, @NotNull Instant instant) {
        boot(entity);
        Number version = null;
        for (Map.Entry<Number, Instant> vers : entity.getAuditRevisions().entrySet()) {
            if (instant.compareTo(vers.getValue()) >= 0) {
                version = vers.getKey();
            } else {
                return Optional.ofNullable(version);
            }
        }
        return Optional.ofNullable(version);
    }

    @Override
    public Optional<Instant> findVersionedAt(@NotNull T entity, @NotNull Integer version) {
        return findRevision(entity, version).flatMap(rev -> findRevisionedAt(entity, rev));
    }

    @Override
    public Optional<Instant> findRevisionedAt(@NotNull T entity, @NotNull Number revision) {
        boot(entity);
        return Optional.ofNullable(entity.getAuditRevisions().get(revision));
    }

    @Override
    public Optional<Integer> findNearestVersion(@NotNull T entity, @NotNull Integer version) {
        return findLatestVersion(entity)
                .map(lv -> {
                    if (version < 0) {
                        return 0;
                    } else if (version > lv) {
                        return lv;
                    } else {
                        return version;
                    }
                });
    }

    @Override
    public Optional<Number> findNearestRevision(@NotNull T entity, @NotNull Number revision) {
        List<Number> allVersions = findAllRevisions(entity);
        double minDiff = Integer.MAX_VALUE;
        Number nearest = null;
        for (Number i : allVersions) {
            double diff = Math.abs(revision.doubleValue() - i.doubleValue());
            if (diff < minDiff) {
                minDiff = diff;
                nearest = i;
            }
        }
        return Optional.ofNullable(nearest);
    }

    @Override
    public Map<Number, Instant> findAllVersionDate(@NotNull T entity) {
        return findLatestVersion(entity)
                .stream()
                .collect(Collectors.toMap(
                        ver -> ver,
                        ver -> findVersionedAt(entity, ver).orElse(Instant.EPOCH)
                ));
    }

    @Override
    public List<Instant> findAllRevisionDate(@NotNull T entity) {
        return findAllRevisions(entity).stream()
                .map(rev -> findRevisionedAt(entity, rev).orElse(Instant.EPOCH))
                .toList();
    }

    private Optional<List<Number>> findAllRevisionsOptional(T entity) {
        return Optional.of(findAllRevisions(entity)).filter(l -> !l.isEmpty());
    }

    @SuppressWarnings("unchecked")
    private LinkedHashMap<Number, Instant> findAllRevisionDates(T entity) {
        AuditQuery query = auditReader().createQuery()
                .forRevisionsOfEntity(entity.getClass(), false, true)
                .add(AuditEntity.id().eq(entity.getId()))
                .addProjection(AuditEntity.revisionProperty("id"))
                .addProjection(AuditEntity.revisionProperty("timestamp"))
                .addOrder(AuditEntity.revisionProperty("timestamp").asc());
        List<Object[]> resultList = query.getResultList();
        LinkedHashMap<Number, Instant> revisionDates = new LinkedHashMap<>();
        for (Object[] resultArray : resultList) {
            Number revisionNumber = (Number) resultArray[0];
            Instant revisionDate = Instant.ofEpochMilli((long) resultArray[1]);
            revisionDates.put(revisionNumber, revisionDate);
        }

        return revisionDates;
    }

    private Map<BigInteger, T> getEntitiesWithRevision(Map<T, Number> entityIdRevisionMap) {
        if (entityIdRevisionMap.isEmpty()) {
            return new HashMap<>();
        }
        Class<T> tClass = (Class<T>) entityIdRevisionMap.keySet().stream().findFirst().get().getClass();
        AuditQuery query = auditReader().createQuery().forRevisionsOfEntity(tClass, false, false);
        AuditId<BigInteger> idProperty = AuditEntity.id("id");
        AuditProperty<Number> revProperty = AuditEntity.revisionNumber("revision");
        for (Map.Entry<T, Number> entry : entityIdRevisionMap.entrySet()) {
            BigInteger entityId = entry.getKey().getId();
            Number revisionNumber = entry.getValue();

            query.add(AuditEntity.and(
                    idProperty.eq(entityId),
                    revProperty.eq(revisionNumber)
            ));
        }

        List<Object> list = query.getResultList();
        return null;
    }
}
