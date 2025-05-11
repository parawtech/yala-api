package tech.rket.shared.infrastructure.persistence.shared;

import tech.rket.shared.infrastructure.persistence.PersistedObject;
import tech.rket.shared.infrastructure.model.id.JID;
import tech.rket.shared.infrastructure.model.id.JIDGenerator;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class JIDGeneratorByClassAnnotation {
    @Getter
    private static final Map<Class<? extends PersistedObject<Long>>, JIDGenerator> generators = new ConcurrentHashMap<>();

    private JIDGeneratorByClassAnnotation() {
    }

    public static Long generate(Class<? extends PersistedObject<Long>> clazz) {
        return get(clazz).generate(Instant.now());
    }

    private static JIDGenerator get(Class<? extends PersistedObject<Long>> clazz) {
        if (!generators.containsKey(clazz)) {
            JID jid = clazz.getAnnotation(JID.class);
            if (jid == null) {
                throw new IllegalStateException(String.format("Class %s has no @%s declared for type.", clazz.getName(), JID.class.getName()));
            }
            JIDGenerator jidGenerator = new JIDGenerator(jid, null, null);
            generators.put(clazz, jidGenerator);
        }
        return generators.get(clazz);
    }
}
