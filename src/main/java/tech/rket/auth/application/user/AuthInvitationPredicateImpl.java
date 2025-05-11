package tech.rket.auth.application.user;

import tech.rket.auth.application.tenant.info.EmailUtils;
import tech.rket.auth.domain.core.service.AuthInvitationPredicate;
import tech.rket.auth.domain.core.tenant.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthInvitationPredicateImpl implements AuthInvitationPredicate {
    private final EmailUtils emailUtils;

    @Override
    public boolean canBeInvited(Tenant tenant, String role, String auth) {
        Optional<String> freeEmailProvider = emailUtils.findFreeEmailProvider(auth);
        if (freeEmailProvider.isPresent() || !tenant.isWorkTenant()) {
            return true;
        } else {
            String domainPart = emailUtils.findDomainPart(auth);
            return tenant.getWorkDomain().equalsIgnoreCase(domainPart);
        }
    }
}
