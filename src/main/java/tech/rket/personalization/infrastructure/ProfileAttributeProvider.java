package tech.rket.personalization.infrastructure;

import java.util.Map;

public interface ProfileAttributeProvider {
    Map<String, Object> getAttributes(ProfileIdentifiers profileIdentifiers);

    String key();
}
