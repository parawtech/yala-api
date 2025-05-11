package tech.rket.storage.application.result;

import tech.rket.storage.domain.value_object.MimeType;

public record ReserveResult(Long id,
                            String characteristic,
                            String key,
                            MimeType mimeType) {
}
