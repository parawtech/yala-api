package tech.rket.personalization.infrastructure;

import java.util.Optional;

public interface ProfileQueryRepository {
    Optional<String> findProfileIdByIdentifiers(ProfileIdentifiers customerIdentifiers);
}
