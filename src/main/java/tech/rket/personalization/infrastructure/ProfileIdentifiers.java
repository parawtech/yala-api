package tech.rket.personalization.infrastructure;

import lombok.Getter;
import tech.rket.shared.infrastructure.context.RequestContext;
import tech.rket.shared.infrastructure.thread_local.AbstractThreadLocalHolder;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProfileIdentifiers extends ArrayList<ProfileIdentifier> {
    @Getter
    private final String tenantId;

    public ProfileIdentifiers(String tenantId, List<ProfileIdentifier> identifiers) {
        super(identifiers);
        this.tenantId = tenantId;
    }


    public static ProfileIdentifiers parse(String tenantId, String customerIdentifiers) {
        List<ProfileIdentifier> traits = new ArrayList<>();
        String[] pairs = customerIdentifiers.split("&");
        int index = 0;
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > -1) {
                String trait = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                traits.add(new ProfileIdentifier(index++, trait, value));
            }
        }
        return new ProfileIdentifiers(tenantId, traits);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ProfileIdentifier entry : this) {
            if (!result.isEmpty()) {
                result.append("&");
            }
            String key = URLEncoder.encode(entry.trait(), StandardCharsets.UTF_8);
            String value = URLEncoder.encode(entry.value(), StandardCharsets.UTF_8);
            result.append(key).append("=").append(value);
        }
        return result.toString();
    }

    public static void set(ProfileIdentifiers profileIdentifiers) {
        AbstractThreadLocalHolder.set("PERSONALIZATION.profileIdentifiers", profileIdentifiers);
    }

    public static ProfileIdentifiers get() {
        return AbstractThreadLocalHolder.get("PERSONALIZATION.profileIdentifiers", ProfileIdentifiers.class);
    }
}
