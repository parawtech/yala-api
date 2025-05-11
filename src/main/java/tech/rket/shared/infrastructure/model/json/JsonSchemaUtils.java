package tech.rket.shared.infrastructure.model.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import org.slf4j.Logger;
import tech.rket.shared.infrastructure.log.JDLogger;
import tech.rket.shared.infrastructure.object_mapper.ObjectMapperGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

public class JsonSchemaUtils {
    private static final Logger log = JDLogger.getLogger(JsonSchemaUtils.class, Map.of("category", "Common"));
    private static final ObjectMapper objectMapper = ObjectMapperGenerator.jsonMapper();

    public static boolean isValid(JsonNode node, String schema) {
        return schemify(schema).validate(node).isEmpty();
    }

    public static boolean isValid(JsonNode node, JsonSchema schema) {
        return schema.validate(node).isEmpty();
    }

    public static JsonSchema schemify(String schema) {
        try {
            String id = objectMapper.readTree(schema).get("$schema").asText().replace("#", "");
            SpecVersion.VersionFlag flag = SpecVersion.VersionFlag.fromId(id)
                    .orElseThrow();
            return JsonSchemaFactory.getInstance(flag).getSchema(schema);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonSchema loadSchema(String schemaName) {
        schemaName = String.format("schema/%s.schema.json", schemaName);
        String jsonSchemaContent;
        ClassLoader classLoader = JsonSchemaUtils.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(schemaName);
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            jsonSchemaContent = scanner.useDelimiter("\\A").next();
        } catch (IOException | NullPointerException e) {
            log.error("JSON-SCHEMA: Cannot load schema {} due to : {}.", schemaName, e.getMessage());
            jsonSchemaContent = null;
        }
        return schemify(jsonSchemaContent);
    }
}
