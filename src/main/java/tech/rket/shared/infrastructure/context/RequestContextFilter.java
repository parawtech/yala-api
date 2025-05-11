package tech.rket.shared.infrastructure.context;

import co.elastic.apm.api.Traced;
import com.ibm.icu.util.Calendar;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.rket.shared.infrastructure.agent.ParsedUserAgent;
import tech.rket.shared.infrastructure.agent.UserAgentParser;
import tech.rket.shared.infrastructure.geoip.GeoIpLocation;
import tech.rket.shared.infrastructure.geoip.GeoIpLocationParser;
import tech.rket.shared.infrastructure.i18n.LocaleUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Supplier;

import static tech.rket.shared.infrastructure.apm.ApmTracedStatic.*;

@Component
@Order(-2)
public class RequestContextFilter extends OncePerRequestFilter {
    @Value("${shared.request-context.filter.x-headers-strategy}")
    private ContextHeaderStrategy xHeadersStrategy = ContextHeaderStrategy.REPLACE;

    @Value("${shared.request-context.filter.enable}")
    private boolean enable = true;

    @Override
    @Traced(value = "Shared.RequestContext.filter", type = TYPE_CUSTOM, subtype = SUBTYPE_SPRING_ + SERVICE, action = ACTION_PROCESS_ + SEARCH)
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!enable) {
            filterChain.doFilter(request, response);
            return;
        }
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        ContextHeaders headers = fetchHeaders(request);
        ParsedUserAgent agent = UserAgentParser.parse(userAgent);
        GeoIpLocation geoIpLocation = GeoIpLocationParser.parse(ip);

        Locale locale = calculateLocale(headers, geoIpLocation);
        TimeZone timeZone = calculateTimezone(headers, geoIpLocation, locale);
        ContextGeoLocation geoLocation = calculateGeoLocation(headers, geoIpLocation, locale);
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTimeZone(com.ibm.icu.util.TimeZone.getTimeZone(timeZone.getID()));
        LocalDateTime utcDateTime = LocalDateTime.now(ZoneId.of("UTC"));
        calendar.setTime(new Date(utcDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()));
        LocalDateTime dateTime = LocalDateTime.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DATE),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND),
                calendar.get(Calendar.MILLISECOND)
        );
        RequestContext.set(new RequestContext(locale, timeZone, agent, geoLocation, dateTime, utcDateTime));
        setResponseHeader(response, RequestContext.get());
        filterChain.doFilter(request, response);
    }

    private void setResponseHeader(HttpServletResponse response, RequestContext requestContext) {
        if (requestContext.locale() != null) {
            response.setHeader("X-LOCALE", requestContext.locale().toLanguageTag());
        }
        if (requestContext.timeZone() != null) {
            response.setHeader("X-TIMEZONE", requestContext.timeZone().getID());
        }
        if (requestContext.location() != null) {
            if (requestContext.location().continent() != null) {
                response.setHeader("X-GEO-CONTINENT", requestContext.location().continent());
            }
            if (requestContext.location().country() != null) {
                response.setHeader("X-GEO-COUNTRY", requestContext.location().country());
            }
            if (requestContext.location().region() != null) {
                response.setHeader("X-GEO-REGION", requestContext.location().region());
            }
            if (requestContext.location().city() != null) {
                response.setHeader("X-GEO-CITY", requestContext.location().city());
            }
        }

    }


    private ContextGeoLocation calculateGeoLocation(ContextHeaders headers, GeoIpLocation geoIpLocation, Locale locale) {
        String continent = strategicValue(headers.continent(), geoIpLocation.continent(), () -> localeValue(locale, "continent"));
        String country = strategicValue(headers.country(), geoIpLocation.country(), locale::getCountry);
        String city = strategicValue(headers.city(), geoIpLocation.city(), () -> localeValue(locale, "city"));
        String region = strategicValue(headers.region(), geoIpLocation.region(), () -> localeValue(locale, "region"));
        return new ContextGeoLocation(continent, country, region, city);
    }

    private String localeValue(Locale locale, String val) {
        return locale.getUnicodeLocaleKeys().contains(val) ? locale.getUnicodeLocaleType("city") : null;
    }

    private TimeZone calculateTimezone(ContextHeaders headers, GeoIpLocation geoIpLocation, Locale locale) {
        TimeZone geoTimeZone = null;
        TimeZone headerTimeZone = null;
        if (geoIpLocation.timeZone() != null) {
            geoTimeZone = TimeZone.getTimeZone(geoIpLocation.timeZone());
        }
        if (headers.timezone() != null) {
            headerTimeZone = TimeZone.getTimeZone(headers.timezone());
        }
        return strategicValue(headerTimeZone, geoTimeZone, () -> TimeZone.getTimeZone(LocaleUtils.timezone(locale)));
    }

    private Locale calculateLocale(ContextHeaders headers, GeoIpLocation geoIpLocation) {
        Locale fallback = LocaleContextHolder.getLocale();
        Locale geoLocale = null;
        Locale headerLocale = null;
        if (geoIpLocation.country() != null) {
            geoLocale = LocaleUtils.toLocale(LocaleUtils.guessLanguage(geoIpLocation.country()) + "_" + geoIpLocation.country(), fallback);
        }
        if (headers.locale() != null) {
            headerLocale = LocaleUtils.toLocale(headers.locale(), fallback);
        } else if (headers.country() != null) {
            headerLocale = LocaleUtils.toLocale(LocaleUtils.guessLanguage(headers.country()) + "_" + headers.country(), fallback);
        }

        return strategicValue(headerLocale, geoLocale, LocaleContextHolder::getLocale);
    }

    private ContextHeaders fetchHeaders(HttpServletRequest request) {
        String continent = request.getHeader("X-GEO-CONTINENT");
        String country = request.getHeader("X-GEO-COUNTRY");
        String city = request.getHeader("X-GEO-CITY");
        String region = request.getHeader("X-GEO-REGION");
        String locale = request.getHeader("X-LOCALE");
        String timezone = request.getHeader("X-TIMEZONE");
        return new ContextHeaders(locale, timezone, continent, country, region, city);
    }

    private <T> T strategicValue(T headerValue, T geoIpValue, Supplier<T> fallbackValue) {
        if (headerValue != null && ((xHeadersStrategy == ContextHeaderStrategy.REPLACE) || (geoIpValue == null && xHeadersStrategy == ContextHeaderStrategy.PROVIDE))) {
            return headerValue;
        } else if (geoIpValue != null) {
            return geoIpValue;
        } else {
            return fallbackValue == null ? null : fallbackValue.get();
        }
    }

}
