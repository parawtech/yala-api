package tech.rket.shared.infrastructure.contract.impl;

import org.mapstruct.Mapper;
import tech.rket.shared.contract.storage.command.ContractStoredFileSearchCriteria;
import tech.rket.shared.contract.storage.enums.ContractMimeType;
import tech.rket.shared.contract.storage.enums.ContractStoredFileAuthType;
import tech.rket.shared.contract.storage.result.*;
import tech.rket.shared.infrastructure.mapstruct.MapstructConfig;
import tech.rket.storage.application.result.FetchIdResult;
import tech.rket.storage.application.result.PatchContentResult;
import tech.rket.storage.application.result.ReserveResult;
import tech.rket.storage.application.result.UploadResult;
import tech.rket.storage.domain.StoredFile;
import tech.rket.storage.domain.query.StoredFileSearchCriteria;
import tech.rket.storage.domain.value_object.MimeType;
import tech.rket.storage.domain.value_object.StoredFileAuthType;

@Mapper(config = MapstructConfig.class)
public interface StorageClientMapper {
    ContractStoredFile convert(StoredFile value);
    ContractPatchContentResult convert(PatchContentResult value);
    ContractReserveResult convert(ReserveResult value);
    ContractUploadResult convert(UploadResult value);
    ContractFetchIdResult convert(FetchIdResult value);
    StoredFileAuthType convert(ContractStoredFileAuthType value);
    MimeType convert(ContractMimeType value);
    StoredFileSearchCriteria convert(ContractStoredFileSearchCriteria value);
}
