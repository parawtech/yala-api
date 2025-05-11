package tech.rket.shared.infrastructure.persistence.mapper;

import tech.rket.shared.core.shared.HasId;
import tech.rket.shared.infrastructure.persistence.PersistedObject;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.lang.reflect.Field;

public interface PersistenceMapper<PERSISTED_OBJECT extends PersistedObject<?>, T extends HasId<?>> {
    T convert(PERSISTED_OBJECT t);


    static boolean set(@NotNull Object t, @NotNull String field, @Nullable Object value) {
        if (t == null) {
            return false;
        }
        Field f = null;
        Class<?> clz = t.getClass();
        do {
            try {
                f = clz.getDeclaredField(field);
                if (!f.canAccess(t)) {
                    f.setAccessible(true);
                }
                f.set(t, value);
            } catch (Exception e) {
                f = null;
            } finally {
                clz = clz.getSuperclass();
            }
        } while (f == null && clz != Object.class);
        return f != null;
    }
}
