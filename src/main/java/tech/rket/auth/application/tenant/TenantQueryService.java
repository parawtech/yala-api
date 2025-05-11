package tech.rket.auth.application.tenant;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tech.rket.auth.application.tenant.exception.TenantDoesNotExistsException;
import tech.rket.auth.application.tenant.info.TenantInfo;
import tech.rket.auth.application.tenant.info.TenantInvitationInfo;
import tech.rket.auth.application.tenant.info.TenantInvitationListInfo;
import tech.rket.auth.domain.core.tenant.Tenant;
import tech.rket.auth.domain.core.tenant.TenantFillRepository;
import tech.rket.auth.domain.core.tenant.entity.Role;
import tech.rket.auth.domain.query.tenant.TenantLite;
import tech.rket.auth.domain.query.tenant.TenantLiteQueryRepository;
import tech.rket.auth.domain.query.tenant.TenantQueryRepository;
import tech.rket.shared.infrastructure.auth.UserLoginInfo;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TenantQueryService {
    private final TenantQueryRepository repository;
    private final TenantLiteQueryRepository liteQueryRepository;
    private final TenantFillRepository tenantFillRepository;

    @Transactional(readOnly = true)
    public Page<TenantLite> getAll(Pageable pageable) {
        return liteQueryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public TenantInfo get(Long tenant) {
        return repository.findById(tenant)
                .map(r -> new TenantInfo(r.getId(), r.getName()))
                .orElseThrow(() -> new TenantDoesNotExistsException(tenant));
    }

    @Transactional(readOnly = true)
    public Set<Role> getRoles(Long id) {
        return repository.findById(id).orElseThrow(() -> new TenantDoesNotExistsException(id)).getRoles();
    }

    @Transactional(readOnly = true)
    public Set<Role> getCurrentTenantRoles() {
        return getRoles(UserLoginInfo.getCurrent().tenantId());
    }

    @Transactional(readOnly = true)
    public TenantInfo getCurrentTenant() {
        return get(UserLoginInfo.getCurrent().tenantId());
    }

    @Transactional(readOnly = true)
    public TenantInvitationListInfo getInvites() {
        Long tenantId = UserLoginInfo.getCurrent().tenantId();
        Tenant tenant = repository.findById(tenantId)
                .orElseThrow(() -> new TenantDoesNotExistsException(tenantId));
        tenantFillRepository.fillJoinInvitations(tenant);
        tenantFillRepository.fillRegisterInvitations(tenant);
        Set<TenantInvitationInfo> tenantInvitationInfos = new LinkedHashSet<>();
        return new TenantInvitationListInfo(tenant.getRegisterInvitations(), tenant.getJoinInvitations());
    }
}
