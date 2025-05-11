package tech.rket.shared.infrastructure.phonelib;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonemetadata;
import com.google.i18n.phonenumbers.Phonenumber;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class PhoneNumberHelper {
    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    private static final Method getMetadataForRegion;

    static {
        try {
            getMetadataForRegion = PhoneNumberUtil.class.getDeclaredMethod("getMetadataForRegion", String.class);
            getMetadataForRegion.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<String> normalize(String rawNumber, String defaultRegion) {
        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse(rawNumber, defaultRegion);

            if (!phoneUtil.isValidNumberForRegion(number, defaultRegion)) {
                return Optional.empty();
            }

            return Optional.of(phoneUtil.format(number, PhoneNumberFormat.E164));
        } catch (NumberParseException e) {
            throw new IllegalArgumentException("Failed to parse number: " + e.getMessage());
        }
    }

    public static Set<String> getSupportedRegions() {
        return phoneUtil.getSupportedRegions();
    }

    public static Map<String, Map<String, Object>> getMobileRegexPerRegion() {
        Map<String, Map<String, Object>> result = new TreeMap<>();

        for (String region : phoneUtil.getSupportedRegions()) {
            Map<String, Object> metadata = getMobileRegexForRegion(region);
            if (!metadata.isEmpty()) {
                result.put(region, metadata);
            }
        }

        return result;
    }

    public static Map<String, Object> getMobileRegexForRegion(String region) {
        Phonemetadata.PhoneMetadata metadata = null;
        try {
            metadata = (Phonemetadata.PhoneMetadata) (getMetadataForRegion.invoke(phoneUtil, region));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (metadata == null) {
            return Map.of();
        }

        Phonemetadata.PhoneNumberDesc mobile = metadata.getMobile();
        Map<String, Object> map = new HashMap<>();
        map.put("length", mobile.getPossibleLengthList());
        map.put("pattern", mobile.getNationalNumberPattern());
        map.put("example", mobile.getExampleNumber());
        try {
            map.put("e164", phoneUtil.format(phoneUtil.parse(mobile.getExampleNumber(), region), PhoneNumberFormat.E164));
        } catch (NumberParseException ignored) {
        }
        return map;
    }

    public static boolean isPhoneNumber(String input, String defaultRegion) {
        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse(input, defaultRegion);
            if (phoneUtil.isValidNumber(number)) {
                return true;
            }
        } catch (NumberParseException ignored) {
        }

        return false;
    }
}
