package tech.rket.shared.infrastructure.urchin;

import co.elastic.apm.api.Traced;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static tech.rket.shared.infrastructure.apm.ApmTracedStatic.*;

@Component
public class UrchinTrackingFilter extends OncePerRequestFilter {
    @Value("${shared.urchin-tracking.filter.enable}")
    private boolean enable = true;

    @Traced(value = "Shared.UrchinTracking.filter", type = TYPE_CUSTOM, subtype = SUBTYPE_SPRING_ + SERVICE, action = ACTION_PROCESS_ + SEARCH)
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!enable) {
            filterChain.doFilter(request, response);
            return;
        }
        String source = request.getHeader("X-PERSONALIZATION-UTM-SOURCE");
        if (source == null || source.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        String medium = request.getHeader("X-PERSONALIZATION-UTM-MEDIUM");
        String campaign = request.getHeader("X-PERSONALIZATION-UTM-CAMPAIGN");
        String content = request.getHeader("X-PERSONALIZATION-UTM-CONTENT");
        String term = request.getHeader("X-PERSONALIZATION-UTM-TERM");

        response.setHeader("X-PERSONALIZATION-UTM-SOURCE", source);
        if (medium != null) {
            response.setHeader("X-PERSONALIZATION-UTM-MEDIUM", medium);
        }
        if (campaign != null) {
            response.setHeader("X-PERSONALIZATION-UTM-CAMPAIGN", campaign);
        }
        if (content != null) {
            response.setHeader("X-PERSONALIZATION-UTM-CONTENT", content);
        }
        if (term != null) {
            response.setHeader("X-PERSONALIZATION-UTM-TERM", term);
        }
        UrchinTracking tracking = new UrchinTracking(source, medium, campaign, content, term);
        UrchinTracking.set(tracking);
        filterChain.doFilter(request, response);
    }
}
