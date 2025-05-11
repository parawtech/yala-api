package tech.rket.payment.domain.shared;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import tech.rket.shared.infrastructure.object_mapper.ObjectMapperGenerator;

import java.util.*;

public class JDAdditionInfo extends HashMap<String, List<Map<String, Object>>> {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperGenerator.jsonMapper();

    public void addAdditionalInfo(String group, Object o, String... keysToRetain) {
        Map<String, Object> map = create(o, keysToRetain);
        this.compute(group, (k, v) -> {
            if (v == null) {
                List<Map<String, Object>> list = new ArrayList<>();
                list.add(map);
                return list;
            } else {
                v.add(map);
                return v;
            }

        });
    }

    private Map<String, Object> create(Object o, String[] keysToRetain) {
        if (o == null) {
            return new HashMap<>();
        }
        Map<String, Object> map = OBJECT_MAPPER.convertValue(o, new TypeReference<Map<String, Object>>() {
        });
        if (keysToRetain.length > 0) {
            map.keySet().retainAll(Arrays.asList(keysToRetain));
        }
        return map;
    }


}
