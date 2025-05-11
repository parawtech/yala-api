package tech.rket.shared.contract.storage.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import tech.rket.shared.contract.storage.enums.ContractMimeType;

import java.util.Map;

public record ContractUploadResult(Long id,
                                   String characteristics,
                                   String key,
                                   long size,
                                   ContractMimeType mimeType,
                                   @JsonInclude(JsonInclude.Include.NON_EMPTY)
                                   Map<String, ContractUploadResult> variants) {
}
