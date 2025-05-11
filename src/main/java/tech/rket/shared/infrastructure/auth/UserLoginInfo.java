package tech.rket.shared.infrastructure.auth;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public record UserLoginInfo(
        UUID sessionId,
        Long userId,
        String userEmail,
        Long tenantId,
        String role,
        Set<String> permissions,
        Locale locale
) implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add((GrantedAuthority) () -> String.format("ROLE_%s", role()));
        authorities.addAll(permissions().stream()
                .map(e -> (GrantedAuthority) () -> String.format("PERMISSION_%s", e.toUpperCase()))
                .toList());
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return userEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static Optional<UserLoginInfo> findCurrent() {
        Object details = Optional.ofNullable(RKetSecurityContextHelper.getAuthenticated()).map(Authentication::getDetails).orElse(null);
        return (details instanceof UserLoginInfo userLoginInfo) ? Optional.of(userLoginInfo) : Optional.empty();
    }

    public static UserLoginInfo getCurrent() {
        return findCurrent().orElseThrow(() -> new AuthenticationCredentialsNotFoundException(""));
    }
}

