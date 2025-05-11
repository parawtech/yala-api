package tech.rket.shared.infrastructure.auth.addon;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import tech.rket.auth.application.user.AuthGodComponent;
import tech.rket.auth.domain.core.user.User;
import tech.rket.shared.infrastructure.auth.UserLoginInfo;

import java.util.UUID;

import static tech.rket.shared.infrastructure.auth.RKetSecurityContextHelper.getAuthenticated;

@Component
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProxiesMethodSecurityExpressionRoot extends SpringAddonsMethodSecurityExpressionRoot {
    private final AuthGodComponent authGodComponent;
    private User god;

    @Autowired
    public ProxiesMethodSecurityExpressionRoot(AuthGodComponent authGodComponent) {
        this.authGodComponent = authGodComponent;
    }

    public boolean isRefreshToken() {
        Authentication authentication = getAuthenticated();
        return authentication != null &&
                !authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof String principal &&
                principal.equals("refreshToken") &&
                authentication.getDetails() instanceof UUID;
    }

    public boolean isAdmin() {
        UserLoginInfo current = UserLoginInfo.getCurrent();
        return current != null && current.tenantId() != null && current.tenantId().equals(godTenantId());
    }

    private Long godTenantId() {
        return god().getDefaultMembership().getTenant().getId();
    }

    private User god() {
        return god == null ? (god = authGodComponent.get()) : god;
    }

    public boolean isGod() {
        UserLoginInfo current = UserLoginInfo.getCurrent();
        return current != null && god().getId().equals(current.userId());
    }

    public boolean hasRole(Long tenantId, String role) {
        UserLoginInfo current = UserLoginInfo.getCurrent();
        return current != null && current.role().equals(role) && current.tenantId().equals(tenantId);
    }

    public boolean hasAuthority(Long tenantId, String authority) {
        UserLoginInfo current = UserLoginInfo.getCurrent();
        return current != null && current.permissions().contains(authority) && current.tenantId().equals(tenantId);
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        return new SpringAddonsMethodSecurityExpressionHandler(() -> new ProxiesMethodSecurityExpressionRoot(authGodComponent, god));
    }
}
