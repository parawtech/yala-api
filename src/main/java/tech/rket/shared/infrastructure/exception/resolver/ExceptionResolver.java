package tech.rket.shared.infrastructure.exception.resolver;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface ExceptionResolver<T extends Throwable, E> {
    Map<String, Map<Class<? extends Throwable>, ExceptionResolver<?, ?>>> MAP = new HashMap<>();

    @Nullable
    E resolve(@Nonnull T ex, @Nonnull WebRequest request);

    default E resolveException(@Nonnull Throwable ex, @Nonnull WebRequest webRequest) {
        return resolve((T) ex, webRequest);
    }

    Class<T> supportedClass();

    @PostConstruct
    default void register() {
        if (!MAP.containsKey(protocol())) {
            MAP.put(protocol(), new HashMap<>());
        }
        MAP.get(protocol()).put(supportedClass(), this);
    }

    @SuppressWarnings("unchecked")
    static <T extends Throwable> Optional<ExceptionResolver<T, ?>> find(Class<T> e, String protocol) {
        Class<?> ex = e;
        Map<Class<? extends Throwable>, ExceptionResolver<?, ?>> map = MAP.getOrDefault(protocol, new HashMap<>());
        ExceptionResolver<?, ?> exceptionResolver;
        do {
            exceptionResolver = map.get(ex);
            ex = ex.getSuperclass();
        } while (ex != null && exceptionResolver == null);
        return Optional.ofNullable((ExceptionResolver<T, ?>) exceptionResolver);
    }

    String protocol();
}
