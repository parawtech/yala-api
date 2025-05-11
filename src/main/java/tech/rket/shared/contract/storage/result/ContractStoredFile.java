package tech.rket.shared.contract.storage.result;

import tech.rket.shared.contract.storage.enums.ContractMimeType;
import tech.rket.shared.contract.storage.enums.ContractStoredFileStatus;

import java.time.Instant;
import java.util.Map;

public record ContractStoredFile(
        Long id,
        Long tenant,
        Long user,
        String characteristic,
        String key,
        Instant createdTime,
        ContractStoredFileAuth auth,
        ContractStoredFileStatus status,
        Long size,
        ContractMimeType mimeType,
        Map<String, Object> metadata,
        ContractStoredFile parent,
        String variantKey) {
}