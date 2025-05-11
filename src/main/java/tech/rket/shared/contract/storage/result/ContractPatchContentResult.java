package tech.rket.shared.contract.storage.result;

import tech.rket.shared.contract.storage.enums.ContractStoredFileStatus;

public record ContractPatchContentResult(Long id, Long size, ContractStoredFileStatus status) {
}
