package tech.rket.shared.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(RedisConfig.class)
public class CacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, Object value, Duration timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }

    public <T> List<T> getList(String... keys) {
        return (List<T>) redisTemplate.opsForValue().multiGet(List.of(keys));
    }

    public <T> List<T> getList(Collection<String> keys) {
        return (List<T>) redisTemplate.opsForValue().multiGet(keys);
    }

    public <T> Optional<T> get(String key) {
        return Optional.ofNullable((T) redisTemplate.opsForValue().get(key));
    }

    public Object evict(String key) {
        return redisTemplate.opsForValue().getAndDelete(key);
    }


    public boolean exists(String key) {
        return get(key).isEmpty();
    }

    public Object refresh(String key, Duration duration) {
        return redisTemplate.opsForValue().getAndExpire(key, duration);
    }

    public void replace(String key, Object v) {
        redisTemplate.opsForValue().set(key, v);
    }

    public void replace(String key, Object v, Duration duration) {
        redisTemplate.opsForValue().set(key, v, duration);
    }

    public Object persist(String key) {
        return redisTemplate.opsForValue().getAndPersist(key);
    }

    public void unset(String key) {
        redisTemplate.opsForValue().getAndDelete(key);
    }
}
