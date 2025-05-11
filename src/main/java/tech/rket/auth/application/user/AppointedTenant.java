package tech.rket.auth.application.user;

import tech.rket.auth.domain.core.tenant.Tenant;

public record AppointedTenant(Tenant tenant, String role) {

}
