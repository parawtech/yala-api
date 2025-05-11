package tech.rket.personalization.infrastructure;

import java.util.LinkedHashSet;
import java.util.Set;

public final class ProfileAttributeProviderPool {
    private static final Set<ProfileAttributeProvider> providers = new LinkedHashSet<>();

    private ProfileAttributeProviderPool() {
    }

    public static Set<ProfileAttributeProvider> get() {
        return providers;
    }

    public static void add(ProfileAttributeProvider provider) {
        providers.add(provider);
    }
    public static void remove(ProfileAttributeProvider provider) {
        providers.remove(provider);
    }
    public static void clear() {
        providers.clear();
    }
}
