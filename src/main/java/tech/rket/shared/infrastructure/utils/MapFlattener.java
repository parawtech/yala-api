package tech.rket.shared.infrastructure.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapFlattener {

    public static Map<String, Object> flattenMap(Map<String, Object> original) {
        Map<String, Object> flattened = new LinkedHashMap<>();
        flatten("", original, flattened);
        return flattened;
    }

    private static void flatten(String prefix, Map<String, Object> map, Map<String, Object> result) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                flatten(key, (Map<String, Object>) value, result);
            } else {
                result.put(key, value);
            }
        }
    }
    public static Map<String, Object> unflattenMap(Map<String, Object> flatMap) {
        Map<String, Object> result = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : flatMap.entrySet()) {
            String[] keys = entry.getKey().split("\\.");
            Map<String, Object> current = result;

            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];

                if (i == keys.length - 1) {
                    current.put(key, entry.getValue());
                } else {
                    if (!current.containsKey(key) || !(current.get(key) instanceof Map)) {
                        current.put(key, new LinkedHashMap<String, Object>());
                    }
                    current = (Map<String, Object>) current.get(key);
                }
            }
        }

        return result;
    }
}
