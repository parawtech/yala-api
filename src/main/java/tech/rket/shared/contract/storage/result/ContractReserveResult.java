package tech.rket.shared.contract.storage.result;

import tech.rket.shared.contract.storage.enums.ContractMimeType;

public record ContractReserveResult(Long id,
                                    String characteristic,
                                    String key,
                                    ContractMimeType mimeType) {
}
