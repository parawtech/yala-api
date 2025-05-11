package tech.rket.shared.infrastructure.mapstruct;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MapperHelper {
    @Named("stringToUUID")
    public UUID stringToUUID(String input) {
        return UUID.fromString(input);
    }
}
