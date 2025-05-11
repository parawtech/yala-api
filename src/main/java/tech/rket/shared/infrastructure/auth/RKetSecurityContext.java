package tech.rket.shared.infrastructure.auth;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class RKetSecurityContext implements Authentication, SecurityContext {
    private ThreadLocal<Authentication> authentication = new ThreadLocal<>();
    private static final RKetSecurityContext INSTANCE = new RKetSecurityContext();

    public static void inject() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL);
        SecurityContextHolder.setContext(INSTANCE);
    }

    public static void set(UserLoginInfo loginInfo) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                loginInfo.userId(),
                null,
                loginInfo.getAuthorities());
        authentication.setDetails(loginInfo);
        INSTANCE.authentication = new ThreadLocal<>();
        INSTANCE.authentication.set(authentication);
    }


    public static void clear() {
        INSTANCE.authentication = null;
    }

    public static void setAnonymous() {
        INSTANCE.authentication = new ThreadLocal<>();
        var authentication = new AnonymousAuthenticationToken(
                "anonymousUser",
                "anonymousUser",
                List.of((GrantedAuthority) () -> "ROLE_ANONYMOUS")
        );
        authentication.setAuthenticated(false);
        INSTANCE.authentication.set(authentication);
    }

    public static void setRefresh(UUID sessionId) {
        AnonymousAuthenticationToken authentication = new AnonymousAuthenticationToken("refreshToken", "refreshToken", List.of((GrantedAuthority) () -> "ROLE_REFRESH_TOKEN"));
        authentication.setDetails(sessionId);
        authentication.setAuthenticated(false);
        INSTANCE.authentication = new ThreadLocal<>();
        INSTANCE.authentication.set(authentication);
    }

    public static boolean isRefreshToken() {
        var authentication = authentication();
        return authentication != null &&
                !authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof String principal &&
                principal.equals("refreshToken") &&
                authentication.getDetails() instanceof UUID;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authentication() == null ? List.of() : authentication().getAuthorities();
    }

    public static Authentication authentication() {
        return INSTANCE.authentication == null || INSTANCE.authentication.get() == null ? null : INSTANCE.authentication.get();
    }

    @Override
    public Object getCredentials() {
        return authentication() == null ? null : authentication().getCredentials();
    }

    @Override
    public Object getDetails() {
        return authentication() == null ? null : authentication().getDetails();
    }

    @Override
    public Object getPrincipal() {
        return authentication() == null ? null : authentication().getPrincipal();
    }

    @Override
    public boolean isAuthenticated() {
        return authentication() != null && authentication().isAuthenticated();
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (authentication() != null) {
            authentication().setAuthenticated(isAuthenticated);
        }
    }

    @Override
    public String getName() {
        return authentication() == null ? null : authentication().getName();
    }

    @Override
    public Authentication getAuthentication() {
        return authentication();
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        this.authentication.set(authentication);
    }
}
