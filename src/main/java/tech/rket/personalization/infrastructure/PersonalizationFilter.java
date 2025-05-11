package tech.rket.personalization.infrastructure;

import co.elastic.apm.api.Traced;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tech.rket.shared.infrastructure.apm.ApmTracedStatic.*;

@Component
@Order(-1)
public class PersonalizationFilter extends OncePerRequestFilter {
    private static final Pattern pattern = Pattern.compile("/tenant/([^/]+)");

    @Value("${personalization.filter.enable}")
    private boolean enable = true;

    @Override
    @Traced(value = "PERSONALIZATION.filter", type = TYPE_CUSTOM, subtype = SUBTYPE_SPRING_ + SERVICE, action = ACTION_PROCESS_ + SEARCH)
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!enable) {
            filterChain.doFilter(request, response);
            return;
        }
        String customerIdentifier = request.getHeader("X-CUSTOMER-IDENTIFIERS");
        if (customerIdentifier == null || customerIdentifier.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }
        String tenantId = extractTenantId(request.getRequestURI());
        if (tenantId == null) {
            throw new AccessDeniedException("Cannot use X-CUSTOMER-IDENTIFIERS while not have specify tenant");
        }
        ProfileIdentifiers identifiers = ProfileIdentifiers.parse(tenantId, customerIdentifier);
        ProfileIdentifiers.set(identifiers);
        if (identifiers.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        Map<String, Object> map = PersonalizationContext.get();
        ProfileAttributeProviderPool.get()
                .forEach(profileAttributeProvider -> map.put(profileAttributeProvider.key(), profileAttributeProvider.getAttributes(identifiers)));
        filterChain.doFilter(request, response);
    }

    private String extractTenantId(String requestURI) {
        Matcher matcher = pattern.matcher(requestURI);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
