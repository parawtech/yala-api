package tech.rket.personalization.infrastructure;

import tech.rket.shared.infrastructure.context.AbstractContextHolder;
import tech.rket.shared.infrastructure.utils.MapFlattener;

import java.util.HashMap;
import java.util.Map;

public final class PersonalizationContext extends HashMap<String, Object> {
    private PersonalizationContext() {

    }

    public Map<String, Object> flatten() {
        return MapFlattener.flattenMap(this);
    }
    public Map<String, Object> unflatten() {
        return MapFlattener.unflattenMap(this);
    }

    public static PersonalizationContext get() {
        PersonalizationContext context = AbstractContextHolder.get("PERSONALIZATION.context", PersonalizationContext.class);
        if (context == null) {
            PersonalizationContext personalizationContext = new PersonalizationContext();
            set(personalizationContext);
            context = personalizationContext;
        }
        return context;
    }

    public static void set(PersonalizationContext context) {
        AbstractContextHolder.set("PERSONALIZATION.context", context);
    }

    public static void set(Map<String, Object> context) {
        PersonalizationContext personalizationContext = new PersonalizationContext();
        personalizationContext.putAll(context);
        AbstractContextHolder.set("PERSONALIZATION.context", personalizationContext);
    }
}
