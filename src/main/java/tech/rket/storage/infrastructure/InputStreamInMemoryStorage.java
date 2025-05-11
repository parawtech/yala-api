package tech.rket.storage.infrastructure;

import tech.rket.shared.core.domain.repository.DomainDependantRepository;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InputStreamInMemoryStorage implements DomainDependantRepository<Long, InputStream> {
    private static final Map<Long, InputStream> MAP = new HashMap<>();

    public void save(Long id, InputStream inputStream) {
        MAP.put(id, inputStream);
    }

    public Optional<InputStream> findById(Long id) {
        return Optional.ofNullable(MAP.get(id));
    }

    @Override
    public boolean has(Long id) {
        return MAP.containsKey(id);
    }

    @Override
    public void deleteById(Long id) {
        MAP.remove(id);
    }
}
