package tech.rket.shared.infrastructure.apm;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Outcome;
import co.elastic.apm.api.Transaction;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tech.rket.shared.infrastructure.auth.UserLoginInfo;

import java.io.IOException;
import java.util.Optional;

@Component
@Order(-1)
public class ApmFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Transaction transaction = ElasticApm.currentTransaction();
        chain.doFilter(request, response);
        Optional<UserLoginInfo> infoOptional = UserLoginInfo.findCurrent();
        if (infoOptional.isPresent()) {
            UserLoginInfo info = infoOptional.get();
            transaction.setUser(info.userId().toString(), info.userEmail(), info.userEmail(), info.tenantId().toString());
            transaction.setLabel("tenant.id", info.tenantId().toString());
            transaction.setLabel("user.id", info.userId().toString());
        }
        if (response instanceof HttpServletResponse httpServletResponse) {
            transaction.setOutcome(findOutcome(httpServletResponse));
        }
    }

    private Outcome findOutcome(HttpServletResponse httpServletResponse) {
        int staus = httpServletResponse.getStatus();
        Outcome outcome = null;
        try {
            HttpStatus httpStatus = HttpStatus.valueOf(staus);
            if (httpStatus.isError()) {
                outcome = Outcome.FAILURE;
            } else {
                outcome = Outcome.SUCCESS;
            }
        } catch (IllegalArgumentException e) {
            outcome = Outcome.UNKNOWN;
        }
        return outcome;
    }
}