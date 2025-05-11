package tech.rket.shared.infrastructure.auth;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

public final class RKetSecurityContextHelper {
    public static void inject(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static Authentication getAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static void inject(UserLoginInfo loginInfo) {
        inject(create(loginInfo));
    }

    public static Authentication create(UserLoginInfo loginInfo) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                loginInfo.userId(),
                null,
                loginInfo.getAuthorities());
        authentication.setDetails(loginInfo);
        return authentication;
    }

    public static Authentication createAnonymous() {
        var authentication = new AnonymousAuthenticationToken(
                "anonymousUser",
                "anonymousUser",
                List.of((GrantedAuthority) () -> "ROLE_ANONYMOUS")
        );
        authentication.setAuthenticated(false);
        return authentication;
    }

    public static void injectAnonymous() {
        inject(createAnonymous());
    }

    public static Authentication createRefreshToken(UUID sessionId) {
        AnonymousAuthenticationToken authentication = new AnonymousAuthenticationToken("refreshToken", "refreshToken", List.of((GrantedAuthority) () -> "ROLE_REFRESH_TOKEN"));
        authentication.setDetails(sessionId);
        authentication.setAuthenticated(false);
        return authentication;
    }

    public static void injectRefreshToken(UUID sessionId) {
        inject(createRefreshToken(sessionId));
    }

    public static void clear() {
        inject((Authentication) null);
    }
}
