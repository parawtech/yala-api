package tech.rket.shared.contract.storage;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import tech.rket.shared.contract.storage.command.ContractStoredFileSearchCriteria;
import tech.rket.shared.contract.storage.enums.ContractMimeType;
import tech.rket.shared.contract.storage.enums.ContractStoredFileAuthType;
import tech.rket.shared.contract.storage.result.*;

import java.io.IOException;
import java.util.List;

public interface StorageClient {
    HttpServletResponse redirect(Long id) throws IOException;

    HttpServletResponse redirectVariant(Long id,
                                        String variantKey) throws IOException;

    ContractUploadResult upload(String characteristics,
                                @Valid @NotNull MultipartFile file,
                                String key,
                                @Valid @NotNull ContractStoredFileAuthType authType,
                                List<String> authValues);

    ContractReserveResult reservation(String characteristics,
                                      String key,
                                      @Valid @NotNull ContractStoredFileAuthType authType,
                                      @Valid @NotNull ContractMimeType mimeType,
                                      List<String> authValues);

    ContractPatchContentResult patchContent(Long id, @Valid @NotNull MultipartFile file);


    ContractFetchIdResult revokeId(String characteristics, String key);

    void head(Long id);

    void delete(Long id);

    Page<ContractStoredFile> panelSearch(@Valid @NotNull ContractStoredFileSearchCriteria criteria, Pageable pageable);

    Page<ContractStoredFile> adminSearch(@Valid @NotNull ContractStoredFileSearchCriteria criteria, Pageable pageable);

    Page<ContractStoredFile> adminDelete(Long id);
}
