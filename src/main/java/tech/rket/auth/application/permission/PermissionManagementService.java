package tech.rket.auth.application.permission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tech.rket.auth.application.permission.command.PermissionCreateCommand;
import tech.rket.auth.application.permission.command.PermissionUpdateCommand;
import tech.rket.auth.application.permission.exception.PermissionDoesNotExistException;
import tech.rket.auth.application.permission.exception.PermissionIsAlreadyExistException;
import tech.rket.auth.domain.core.permission.Permission;
import tech.rket.auth.domain.core.permission.PermissionRepository;
import tech.rket.auth.domain.core.permission.command.PermissionCreate;
import tech.rket.auth.domain.core.permission.command.PermissionUpdate;
import tech.rket.shared.infrastructure.persistence.shared.DomainConstraintViolationException;

@Component
@RequiredArgsConstructor
public class PermissionManagementService {
    private final PermissionRepository repository;

    public void create(PermissionCreateCommand command) {
        ensureNew(command.id());
        Permission permission = Permission.create(new PermissionCreate(command.id(), command.name(), command.description()))
                .throwIfFailure(DomainConstraintViolationException::new).value();
        repository.save(permission);
    }

    private void ensureNew(String identifier) {
        if (repository.existsById(identifier)) {
            throw new PermissionIsAlreadyExistException(identifier);
        }
    }

    public void delete(String value) {
        repository.deleteById(value);
    }

    public void update(String identifier, PermissionUpdateCommand command) {
        Permission permission = repository.findById(identifier).orElseThrow(() -> new PermissionDoesNotExistException(identifier));
        permission.update(new PermissionUpdate(command.name(), command.description()))
                .throwIfFailure(DomainConstraintViolationException::new);
        repository.save(permission);
    }
}
