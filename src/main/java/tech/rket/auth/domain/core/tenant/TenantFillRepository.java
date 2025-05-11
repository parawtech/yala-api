package tech.rket.auth.domain.core.tenant;


public interface TenantFillRepository {
    void fillJoinInvitations(Tenant tenant);

    void fillRegisterInvitations(Tenant tenant);
}
