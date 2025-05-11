package tech.rket.auth.application.permission;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import tech.rket.auth.application.permission.exception.PermissionDoesNotExistException;
import tech.rket.auth.domain.core.permission.Permission;
import tech.rket.auth.domain.query.permission.PermissionLite;
import tech.rket.auth.domain.query.permission.PermissionLiteQueryRepository;
import tech.rket.auth.domain.query.permission.PermissionQueryRepository;

@Component
@RequiredArgsConstructor
public class PermissionQueryService {
    private final PermissionLiteQueryRepository liteQueryRepository;
    private final PermissionQueryRepository queryRepository;

    public Page<PermissionLite> getAll(Pageable pageable) {
        return liteQueryRepository.findAll(pageable);
    }

    public Permission get(String value) {
        return queryRepository.findById(value).orElseThrow(() -> new PermissionDoesNotExistException(value));
    }
}
