package tech.rket.shared.infrastructure.context;

import com.ibm.icu.util.Calendar;
import tech.rket.shared.infrastructure.agent.ParsedUserAgent;
import tech.rket.shared.infrastructure.thread_local.AbstractThreadLocalHolder;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.TimeZone;

public record RequestContext(
        Locale locale,
        TimeZone timeZone,
        ParsedUserAgent agent,
        ContextGeoLocation location,
        LocalDateTime localDateTime,
        LocalDateTime utcDateTime) {
    public static void set(RequestContext requestContext) {
        AbstractThreadLocalHolder.set("i18n", requestContext);
    }

    public static RequestContext get() {
        return AbstractThreadLocalHolder.get("i18n", RequestContext.class);
    }

    public String weekDay() {
        Calendar calendar = Calendar.getInstance(
                com.ibm.icu.util.TimeZone.getTimeZone(timeZone.getID()),
                locale);
        calendar.setTimeInMillis(utcDateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
        return DayOfWeek.of(calendar.get(Calendar.DAY_OF_WEEK)).name();
    }
}
