package tech.rket.storage.application.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import tech.rket.storage.domain.value_object.MimeType;

import java.util.Map;

public record UploadResult(@JsonFormat(shape = JsonFormat.Shape.STRING) Long id,
                           String characteristics,
                           String key,
                           long size,
                           MimeType mimeType,
                           @JsonInclude(JsonInclude.Include.NON_EMPTY)
                           Map<String, UploadResult> variants) {
}
