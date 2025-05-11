package tech.rket.shared.jsonlogic;

import io.github.jamsesso.jsonlogic.JsonLogic;
import io.github.jamsesso.jsonlogic.JsonLogicException;
import org.springframework.stereotype.Component;

@Component
public class JsonLogicUtil {
    public static final JsonLogic JSON_LOGIC = new JsonLogic();

    public static Object evaluate(String logic, Object values) {
        try {
            return JSON_LOGIC.apply(logic, values);
        } catch (JsonLogicException e) {
            throw new RuntimeException(e);
        }
    }
}
