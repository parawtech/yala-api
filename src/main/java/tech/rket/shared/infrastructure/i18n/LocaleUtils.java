package tech.rket.shared.infrastructure.i18n;

import com.ibm.icu.impl.CalendarUtil;
import com.ibm.icu.impl.locale.KeyTypeData;
import com.ibm.icu.text.NumberingSystem;
import com.ibm.icu.util.LocaleData;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;

import java.util.Currency;
import java.util.IllformedLocaleException;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class LocaleUtils {
    private LocaleUtils() {
    }

    private static final Pattern pattern = Pattern.compile("^(?:[a-zA-Z]{2,3}(?:[-|_][a-zA-Z]{4})?(?:[-|_][a-zA-Z]{2})?)?(@\\w+=[^;]+(?:;\\w+=[^;]+)*)?$");

    private static String calendar(ULocale locale) {
        if (locale.getCountry().isBlank()) {
            return null;
        }
        String calendar = Optional.ofNullable(locale.getKeywordValue("calendar"))
                .orElseGet(() -> CalendarUtil.getCalendarType(locale));
        if (calendar.equalsIgnoreCase("gregorian")) {
            return "yes";
        }
        return calendar;
    }

    private static String numbers(ULocale locale) {
        if (locale.getCountry().isBlank()) {
            return null;
        }
        return Optional.ofNullable(locale.getKeywordValue("number"))
                .orElseGet(() -> NumberingSystem.getInstance(locale).getName());
    }

    private static String currency(ULocale locale) {
        if (locale.getCountry().isBlank()) {
            return null;
        }
        return Optional.ofNullable(locale.getKeywordValue("currency"))
                .orElseGet(() -> Currency.getInstance(locale.toLocale()).getCurrencyCode());
    }

    private static String timezone(ULocale locale) {
        return Optional.ofNullable(locale.getKeywordValue("timezone"))
                .orElseGet(() -> TimeZone.getDefault().getID());
    }

    private static String bcpTimezone(ULocale uLocale) {
        if (uLocale.getCountry().isBlank()) {
            return null;
        }
        String timezone = timezone(uLocale);
        return KeyTypeData.toBcpType("tz", timezone, null, null);
    }


    private static String measure(ULocale locale) {
        if (locale.getCountry().isBlank()) {
            return null;
        }
        return Optional.ofNullable(locale.getKeywordValue("measure"))
                .orElseGet(() -> {
                    LocaleData.MeasurementSystem system = LocaleData.getMeasurementSystem(locale);
                    if (LocaleData.MeasurementSystem.UK == system) {
                        return "uksystem";
                    } else if (LocaleData.MeasurementSystem.US == system) {
                        return "ussystem";
                    }
                    return "metric";
                });
    }

    public static String timezone(Locale locale) {
        return timezone(ULocale.forLocale(locale));
    }

    private static boolean isSame(String unicodeKey, ULocale partialULocale, ULocale incomingULocale) {
        String partial = partialULocale.getKeywordValue(unicodeKey);
        String incoming = valueFinder(unicodeKey).apply(incomingULocale);
        if (partial == null && incoming == null) {
            return true;
        } else if (partial != null && partial.equalsIgnoreCase(incoming)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isValid(String locale) {
        return pattern.matcher(locale).matches();
    }

    public static Locale toLocale(String incomingLocale) {
        if (!isValid(incomingLocale)) {
            throw new IllformedLocaleException();
        }
        return new ULocale(incomingLocale).toLocale();
    }

    public static Locale toLocale(String incomingLocale, Locale defaultLocale) {
        return toULocale(incomingLocale, defaultLocale).toLocale();
    }

    public static String guessLanguage(String country) {
        return ULocale.addLikelySubtags(new ULocale("", country)).getLanguage();
    }

    private static ULocale toULocale(String incomingLocale, Locale defaultLocale) {
        if (!isValid(incomingLocale)) {
            return toULocale(defaultLocale.toLanguageTag(), Locale.getDefault());
        }
        ULocale uLocale = new ULocale(incomingLocale);
        ULocale.Builder builder = new ULocale.Builder();

        String language = uLocale.getLanguage().isBlank() ? defaultLocale.getLanguage() : uLocale.getLanguage();
        String country = uLocale.getCountry().isBlank() ? defaultLocale.getCountry() : uLocale.getCountry();
        language = language.isBlank() ? Locale.getDefault().getLanguage() : language;
        country = country.isBlank() ? Locale.getDefault().getCountry() : country;
        builder.setLanguage(language)
                .setRegion(country);

        if (!uLocale.getScript().isBlank()) {
            builder.setScript(uLocale.getScript());
        }

        for (Character c : uLocale.getExtensionKeys()) {
            builder.setExtension(c, uLocale.getExtension(c));
        }

        return builder.build();
    }


    public static boolean support(Locale incomingLocale, String partialLocale) {
        ULocale partialULocale = new ULocale(partialLocale);
        ULocale incomingULocale = ULocale.forLocale(incomingLocale);
        if (!partialULocale.getLanguage().isBlank() && !partialULocale.getLanguage().equals(incomingULocale.getLanguage())) {
            return false;
        }

        if (!partialULocale.getCountry().isBlank() && !partialULocale.getCountry().equals(incomingULocale.getCountry())) {
            return false;
        }

        if (!partialULocale.getScript().isBlank() && !partialULocale.getScript().equals(incomingULocale.getScript())) {
            return false;
        }

        if (!partialULocale.getExtensionKeys().stream()
                .filter(ex -> ex != 'u')
                .allMatch(ex -> partialULocale.getExtension(ex).equals(incomingULocale.getExtension(ex)))) {
            return false;
        }

        return partialULocale.getUnicodeLocaleKeys().stream()
                .map(KeyTypeData::toLegacyKey)
                .allMatch(unicodeKey -> isSame(unicodeKey, partialULocale, incomingULocale));
    }

    private static Function<ULocale, String> valueFinder(String key) {
        return switch (key) {
            case "calendar" -> LocaleUtils::calendar;
            case "currency" -> LocaleUtils::currency;
            case "numbers" -> LocaleUtils::numbers;
            case "timezone" -> LocaleUtils::timezone;
            case "measure" -> LocaleUtils::measure;
            default -> (l) -> l.getKeywordValue(key);
        };
    }
}
