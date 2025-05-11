package tech.rket.shared.infrastructure.auth;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import tech.rket.personalization.infrastructure.ProfileIdentifiers;

import java.util.Optional;

public record CustomerInfo(ProfileIdentifiers identifiers) {
    public static Optional<CustomerInfo> findCurrent() {
        Object details = RKetSecurityContextHelper.getAuthenticated().getDetails();
        return (details instanceof CustomerInfo userLoginInfo) ? Optional.of(userLoginInfo) : Optional.empty();
    }

    public static CustomerInfo getCurrent() {
        return findCurrent().orElseThrow(() -> new AuthenticationCredentialsNotFoundException(""));
    }
}

