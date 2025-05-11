package tech.rket.shared.infrastructure.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RouteAuthorizationFilter implements RequestMatcher {
    private final RequestMatcher tenantMatcher = new AntPathRequestMatcher("/tenant/{tenantId}/workspace/{workspace}/**");
    private final RequestMatcher adminMatcher = new AntPathRequestMatcher("/admin/**");
    private final RequestMatcher panelMatcher = new AntPathRequestMatcher("/panel/**");

    @Override
    public boolean matches(HttpServletRequest request) {
        if (tenantMatcher.matches(request)) {
            if (request.getHeader("X-CUSTOMER-IDENTIFIERS") == null) {
                return false;
            }
            request.setAttribute("CUSTOMER-REQUEST", true);
        } else if (adminMatcher.matches(request)) {
            return isAdmin();
        } else if (panelMatcher.matches(request)) {
            return isAuthenticated();
        }

        return true;
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }


    private boolean isAdmin() {
        return true;
    }
}
