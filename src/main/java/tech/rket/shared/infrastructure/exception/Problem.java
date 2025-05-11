package tech.rket.shared.infrastructure.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import tech.rket.shared.infrastructure.object_mapper.ObjectMapperGenerator;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(Problem.ParamList.class)
public @interface Problem {
    ObjectMapper OBJECT_MAPPER = ObjectMapperGenerator.jsonMapper();
    String PROTOCOL_REST = "rest";
    String PROTOCOL_GRPC = "grpc";
    String PROTOCOL_GRAPHQL = "graphql";

    String METADATA_HEADER = "header";
    String METADATA_COOKIE = "cookie";

    /**
     * The protocol this problem should translate
     */
    String protocol() default PROTOCOL_REST;

    /**
     * The problem code. If empty it used default algorithm
     */
    String code() default "";


    /**
     * The problem root inclusion. all included field include in response root.
     */
    String[] root() default {};

    /**
     * The problem root inclusion. all included field include in response details.*.
     */
    String[] details() default {};

    /**
     * The problem status code, dependant in protocol if rest can be number or status profile in capital case, like NOT_FOUND or 404
     */
    String status() default "";

    /**
     * metadata like headers and cookies in http response.
     */
    MetaData[] metadata() default {};

    @Target({TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface ParamList {
        Problem[] value();
    }

    @interface MetaData {
        String field() default "";

        String name() default "";

        String as() default "header";
    }

    class Utils {
        public static void handleRoot(String[] inclusion, Map<String, Object> map, Object o) {
            if (inclusion.length > 0) {
                ObjectNode objectNode = OBJECT_MAPPER.convertValue(o, ObjectNode.class);
                List<String> list = Arrays.asList(inclusion);
                StreamSupport.stream(((Iterable<String>) objectNode::fieldNames).spliterator(), false)
                        .filter(list::contains)
                        .forEach(f -> map.put(f, objectNode.get(f).asText()));
            }
        }

        public static void handleDetail(String[] inclusion, List<Object> listOfDetails, Object o) {
            if (inclusion.length > 0) {
                ObjectNode objectNode = OBJECT_MAPPER.convertValue(o, ObjectNode.class);
                List<String> list = Arrays.asList(inclusion);
                StreamSupport.stream(((Iterable<String>) objectNode::fieldNames).spliterator(), false)
                        .filter(list::contains)
                        .forEach(f -> listOfDetails.add(toObject(objectNode, f)));
            }
        }

        private static Object toObject(ObjectNode objectNode, String f) {
            Object value;
            JsonNode jn = objectNode.get(f);
            if (jn == null || jn.isNull()) {
                value = null;
            } else if (jn.isTextual()) {
                value = jn.asText();
            } else if (jn.isObject()) {
                value = jn;
            } else if (jn.isArray()) {
                value = jn;
            } else if (jn.isInt()) {
                value = jn.asInt();
            } else if (jn.isLong()) {
                value = jn.asLong();
            } else if (jn.isFloat()) {
                value = (float) jn.asDouble();
            } else if (jn.isDouble()) {
                value = jn.asDouble();
            } else if (jn.isBoolean()) {
                value = jn.asBoolean();
            } else {
                value = jn;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("key", f);
            map.put("value", value);
            return map;
        }
    }
}