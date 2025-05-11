package tech.rket.shared.infrastructure.contract.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import tech.rket.shared.contract.ContractHttpResponse;
import tech.rket.shared.contract.storage.StorageClient;
import tech.rket.shared.contract.storage.command.ContractStoredFileSearchCriteria;
import tech.rket.shared.contract.storage.enums.ContractMimeType;
import tech.rket.shared.contract.storage.enums.ContractStoredFileAuthType;
import tech.rket.shared.contract.storage.result.*;
import tech.rket.shared.infrastructure.contract.RestContractRunner;
import tech.rket.storage.presentation.rest.StorageController;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StorageClientImpl implements StorageClient {
    private final RestContractRunner contractRunner;
    private final StorageController storageController;
    private final StorageClientMapper mapper;

    @Override
    public HttpServletResponse redirect(Long id) throws IOException {
        return contractRunner.runDirect(() -> {
            ContractHttpResponse response = new ContractHttpResponse();
            try {
                storageController.redirect(id, response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return response;
        });
    }

    @Override
    public HttpServletResponse redirectVariant(Long id, String variantKey) {
        return contractRunner.runDirect(() -> {
            ContractHttpResponse response = new ContractHttpResponse();
            try {
                storageController.redirectVariant(id, variantKey, response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return response;
        });
    }

    @Override
    public ContractUploadResult upload(String characteristics, MultipartFile file, String key, ContractStoredFileAuthType authType, List<String> authValues) {
        return mapper.convert(contractRunner.run(() -> storageController.upload(characteristics, file, key, mapper.convert(authType), authValues)));
    }

    @Override
    public ContractReserveResult reservation(String characteristics, String key, ContractStoredFileAuthType authType, ContractMimeType mimeType, List<String> authValues) {
        return mapper.convert(contractRunner.run(() -> storageController.reservation(characteristics, key, mapper.convert(authType), mapper.convert(mimeType), authValues)));
    }

    @Override
    public ContractPatchContentResult patchContent(Long id, MultipartFile file) {
        return mapper.convert(contractRunner.run(() -> storageController.patchContent(id, file)));
    }

    @Override
    public ContractFetchIdResult revokeId(String characteristics, String key) {
        return mapper.convert(contractRunner.run(() -> storageController.revokeId(characteristics, key)));
    }

    @Override
    public void head(Long id) {
        contractRunner.run(() -> storageController.head(id));
    }

    @Override
    public void delete(Long id) {
        contractRunner.run(() -> storageController.delete(id));
    }

    @Override
    public Page<ContractStoredFile> panelSearch(ContractStoredFileSearchCriteria criteria, Pageable pageable) {
        return contractRunner.run(() -> storageController.panelSearch(mapper.convert(criteria), pageable)).map(mapper::convert);
    }

    @Override
    public Page<ContractStoredFile> adminSearch(ContractStoredFileSearchCriteria criteria, Pageable pageable) {
        return contractRunner.run(() -> storageController.adminSearch(mapper.convert(criteria), pageable)).map(mapper::convert);
    }

    @Override
    public Page<ContractStoredFile> adminDelete(Long id) {
        return contractRunner.run(() -> storageController.adminDelete(id)).map(mapper::convert);
    }
}
