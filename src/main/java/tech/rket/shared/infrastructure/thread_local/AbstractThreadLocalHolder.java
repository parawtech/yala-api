package tech.rket.shared.infrastructure.thread_local;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AbstractThreadLocalHolder {
    private static final ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(HashMap::new);

    public static void set(String key, Object value) {
        threadLocal.get().put(key, value);
    }

    public static <T> T get(String key, Class<T> type) {
        return type.cast(threadLocal.get().get(key));
    }

    public static void remove(String key) {
        threadLocal.get().remove(key);
    }

    public static void clear() {
        threadLocal.remove();
    }
}
