package tech.rket.auth.application.tenant.info;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EmailUtils {
    private static final Map<String, String> FREE_EMAIL_PROVIDERS = new LinkedHashMap<>();
    private static final Pattern EMAIL = Pattern.compile("([a-z0-9!#$%&'*+=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@((?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])");

    public static String[] emailProviders() {
        return FREE_EMAIL_PROVIDERS.values().toArray(new String[0]);
    }

    static {
        FREE_EMAIL_PROVIDERS.put("Gmail", "gmail.com");
        FREE_EMAIL_PROVIDERS.put("Yahoo", "yahoo.com");
        FREE_EMAIL_PROVIDERS.put("Outlook", "outlook.com");
        FREE_EMAIL_PROVIDERS.put("ProtonMail", "protonmail.com");
        FREE_EMAIL_PROVIDERS.put("iCloud", "icloud.com");
        FREE_EMAIL_PROVIDERS.put("AOL", "aol.com");
        FREE_EMAIL_PROVIDERS.put("Mail", "mail.com");
        FREE_EMAIL_PROVIDERS.put("Zoho", "zoho.com");
        FREE_EMAIL_PROVIDERS.put("GMX", "gmx.com");
        FREE_EMAIL_PROVIDERS.put("Yandex", "yandex.com");
        FREE_EMAIL_PROVIDERS.put("NeoMailbox", "neomailbox.com");
        FREE_EMAIL_PROVIDERS.put("Tutanota", "tutanota.com");
        FREE_EMAIL_PROVIDERS.put("Mailfence", "mailfence.com");
        FREE_EMAIL_PROVIDERS.put("MozillaThunderbird", "mozillathunderbird.net");
    }

    public Optional<String> findFreeEmailProvider(@NotNull @Email String email) {
        Matcher matcher = EMAIL.matcher(email.toLowerCase());
        if (matcher.matches()) {
            return FREE_EMAIL_PROVIDERS.entrySet().stream()
                    .filter(e -> e.getValue().equalsIgnoreCase(matcher.group(2)))
                    .map(Map.Entry::getKey)
                    .findFirst();
        }
        // shall not pass here
        throw new IllegalArgumentException("Email is invalid");
    }

    public String findLocalPart(@NotNull @Email String email) {
        Matcher matcher = EMAIL.matcher(email.toLowerCase());
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("Email is invalid");
        }
    }

    public String findDomainPart(@NotNull @Email String email) {
        Matcher matcher = EMAIL.matcher(email.toLowerCase());
        if (matcher.matches()) {
            return matcher.group(2);
        } else {
            throw new IllegalArgumentException("Email is invalid");
        }
    }
}
