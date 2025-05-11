package tech.rket.auth.application.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tech.rket.auth.application.tenant.exception.UserNotInvitedInThisTenantAlready;
import tech.rket.auth.application.tenant.info.EmailUtils;
import tech.rket.auth.domain.core.tenant.Tenant;
import tech.rket.auth.domain.core.tenant.TenantFillRepository;
import tech.rket.auth.domain.core.tenant.TenantRepository;
import tech.rket.auth.domain.core.tenant.command.TenantCreate;
import tech.rket.auth.domain.core.tenant.entity.Role;
import tech.rket.auth.domain.core.tenant.entity.TenantRegisterInvitation;
import tech.rket.shared.infrastructure.persistence.shared.DomainConstraintViolationException;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DefaultTenantFinder {
    private final EmailUtils emailUtils;
    private final TenantRepository tenantRepository;
    private final TenantFillRepository tenantFillRepository;

    public AppointedTenant appoint(String auth, Long tenantId) {
        Optional<Tenant> invitedTenant = Optional.ofNullable(tenantId).flatMap(tenantRepository::findById);
        if (invitedTenant.isPresent()) {
            tenantFillRepository.fillRegisterInvitations(invitedTenant.get());
            return new AppointedTenant(invitedTenant.get(), findInvitedRole(auth, invitedTenant.get()));
        }

        Optional<String> freeEmailDomain = emailUtils.findFreeEmailProvider(auth);
        TenantCreate tenantCreate;
        if (freeEmailDomain.isPresent()) {
            tenantCreate = new TenantCreate(tenantRepository.generateID(), auth, null);
        } else {
            String domainPart = emailUtils.findDomainPart(auth);
            boolean isDefaultWorkDomainTenantExists = tenantRepository.findByWorkDomain(domainPart).isPresent();
            if (isDefaultWorkDomainTenantExists) {
                tenantCreate = new TenantCreate(tenantRepository.generateID(), auth, null);
            } else {
                tenantCreate = new TenantCreate(tenantRepository.generateID(), auth, domainPart);
            }
        }
        Tenant tenant = Tenant.create(tenantCreate)
                .throwIfFailure(DomainConstraintViolationException::new)
                .value();
        return new AppointedTenant(tenant, "owner");
    }

    private String findInvitedRole(String auth, Tenant t) {
        tenantFillRepository.fillRegisterInvitations(t);
        return t.findLastRegisterInvitation(auth)
                .map(TenantRegisterInvitation::role)
                .map(Role::getId)
                .orElseThrow(() -> new UserNotInvitedInThisTenantAlready(auth));
    }
}
