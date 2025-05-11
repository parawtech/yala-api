package tech.rket.shared.infrastructure.log;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tech.rket.shared.infrastructure.auth.UserLoginInfo;

import java.io.IOException;
import java.util.Optional;

@Component
@Order(-1)
public class MdcFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            MDC.put("host.ip", request.getRemoteHost());
            if (request instanceof HttpServletRequest httpServletRequest) {
                MDC.put("http.request.method", httpServletRequest.getMethod());
                MDC.put("user_agent.original", httpServletRequest.getHeader("USER-AGENT"));
                MDC.put("url.path", httpServletRequest.getRequestURI());
                if (httpServletRequest.getQueryString() != null) {
                    MDC.put("url.query", httpServletRequest.getQueryString());
                }
                if (httpServletRequest.getHeader("X-Correlation-ID") != null) {
                    MDC.put("http.request.id", httpServletRequest.getHeader("X-Correlation-ID"));
                } else if (httpServletRequest.getHeader("X-Request-ID") != null) {
                    MDC.put("http.request.id", httpServletRequest.getHeader("X-Request-ID"));
                }
                if (httpServletRequest.getHeader("Content-Type") != null) {
                    MDC.put("http.request.mime_type", httpServletRequest.getHeader("Content-Type"));
                }
            }

            Optional<UserLoginInfo> infoOptional = UserLoginInfo.findCurrent();
            if (infoOptional.isPresent()) {
                UserLoginInfo info = infoOptional.get();
                MDC.put("tenant.id", info.tenantId().toString());
                MDC.put("user.profile", info.userEmail());
                MDC.put("user.id", info.userId().toString());
                MDC.put("user.roles", info.role());
                MDC.put("user.permissions", String.join(",", info.permissions().toArray(new String[0])));
            }

            chain.doFilter(request, response);
        } finally {
            MDC.remove("host.ip");
            MDC.remove("user_agent.original");
            MDC.remove("url.path");
            MDC.remove("url.query");
            MDC.remove("http.request.id");
            MDC.remove("http.request.mime_type");
            MDC.remove("tenant.id");
            MDC.remove("user.id");
            MDC.remove("user.profile");
            MDC.remove("user.roles");
            MDC.remove("user.permissions");
        }
    }
}