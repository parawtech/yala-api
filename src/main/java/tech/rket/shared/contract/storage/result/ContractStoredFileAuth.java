package tech.rket.shared.contract.storage.result;

import tech.rket.shared.contract.storage.enums.ContractStoredFileAuthType;

import java.util.List;

public record ContractStoredFileAuth(
        ContractStoredFileAuthType type,
        Long userId,
        Long tenantId,
        List<String> authValues) {
}