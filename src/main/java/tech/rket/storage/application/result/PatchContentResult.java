package tech.rket.storage.application.result;

import tech.rket.storage.domain.value_object.StoredFileStatus;

public record PatchContentResult(Long id, Long size, StoredFileStatus status) {
}
