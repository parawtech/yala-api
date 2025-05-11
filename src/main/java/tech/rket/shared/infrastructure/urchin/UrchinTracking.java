package tech.rket.shared.infrastructure.urchin;

import tech.rket.shared.infrastructure.thread_local.AbstractThreadLocalHolder;

public record UrchinTracking(String source, String medium, String campaign, String content, String term) {
    public static void set(UrchinTracking context) {
        AbstractThreadLocalHolder.set("PERSONALIZATION.Urchin", context);
    }

    public static UrchinTracking get() {
        return AbstractThreadLocalHolder.get("PERSONALIZATION.Urchin", UrchinTracking.class);
    }
}
