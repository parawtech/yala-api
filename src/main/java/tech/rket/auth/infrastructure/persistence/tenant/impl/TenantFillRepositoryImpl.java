package tech.rket.auth.infrastructure.persistence.tenant.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tech.rket.auth.domain.core.tenant.Tenant;
import tech.rket.auth.domain.core.tenant.TenantFillRepository;
import tech.rket.auth.infrastructure.persistence.tenant.entity.RegisterInvitationEntity;
import tech.rket.auth.infrastructure.persistence.tenant.impl.mapper.TenantHibernateMapper;
import tech.rket.auth.infrastructure.persistence.tenant.repository.RegisterInvitationEntityRepository;
import tech.rket.auth.infrastructure.persistence.tenant.entity.TenantEntity;
import tech.rket.auth.infrastructure.persistence.tenant.repository.TenantEntityRepository;
import tech.rket.auth.infrastructure.persistence.user.repository.InvitationEntityRepository;

import java.util.Comparator;

@Repository
@RequiredArgsConstructor
public class TenantFillRepositoryImpl
        implements TenantFillRepository {
    private final TenantEntityRepository tenantEntityRepository;
    private final TenantHibernateMapper mapper;
    private final InvitationEntityRepository invitationEntityRepository;
    private final RegisterInvitationEntityRepository registerInvitationEntityRepository;

    @Override
    public void fillJoinInvitations(Tenant tenant) {
        TenantEntity tenantEntity = tenantEntityRepository.findById(tenant.getId()).orElseThrow();
        invitationEntityRepository.findAllByTenant(tenantEntity).stream()
                .map(mapper::convert)
                .forEach(tenant.getJoinInvitations()::add);
    }

    @Override
    public void fillRegisterInvitations(Tenant tenant) {
        registerInvitationEntityRepository.findAllByRole_Tenant_Id(tenant.getId()).stream()
                .sorted(Comparator.comparingLong(RegisterInvitationEntity::getId))
                .map(mapper::convert)
                .forEach(tenant.getRegisterInvitations()::add);
    }
}
