package tech.rket.metric.application;

import java.time.Duration;
import java.time.Instant;

public class TimeRoundingUtils {

    private static final long FIVE_MINUTES_IN_SECONDS = Duration.ofMinutes(5).getSeconds();

    public static Instant roundDownToFiveMinutes(Instant time) {
        long epochSeconds = time.getEpochSecond();
        long roundedSeconds = epochSeconds - (epochSeconds % FIVE_MINUTES_IN_SECONDS);
        return Instant.ofEpochSecond(roundedSeconds);
    }

    public static Instant roundUpToFiveMinutes(Instant time) {
        long epochSeconds = time.getEpochSecond();
        long remainder = epochSeconds % FIVE_MINUTES_IN_SECONDS;
        if (remainder == 0) {
            return time;
        }
        long roundedSeconds = epochSeconds + (FIVE_MINUTES_IN_SECONDS - remainder);
        return Instant.ofEpochSecond(roundedSeconds);
    }
}
