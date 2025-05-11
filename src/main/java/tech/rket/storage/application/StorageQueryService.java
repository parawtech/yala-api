package tech.rket.storage.application;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import tech.rket.shared.infrastructure.auth.UserLoginInfo;
import tech.rket.shared.infrastructure.restfilter.filter.LongFilter;
import tech.rket.shared.infrastructure.restfilter.filter.StringFilter;
import tech.rket.storage.application.command.StoredFileFetchHashCommand;
import tech.rket.storage.application.exception.StoredFileDoesNotFoundException;
import tech.rket.storage.application.exception.StoredFileIsNotReadyException;
import tech.rket.storage.application.result.FetchIdResult;
import tech.rket.storage.domain.StoredFile;
import tech.rket.storage.domain.entity.StoredFileContent;
import tech.rket.storage.domain.entity.StoredFileTemporaryUrl;
import tech.rket.storage.domain.query.StoredFileQueryRepository;
import tech.rket.storage.domain.query.StoredFileSearchCriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static tech.rket.storage.domain.value_object.StoredFileStatus.UPLOADED;

@Service
@RequiredArgsConstructor
public class StorageQueryService {
    private final StoredFileQueryRepository queryRepository;

    public StoredFileTemporaryUrl download(@Valid @NotNull Long id) {
        StoredFile file = queryRepository.findById(id)
                .orElseThrow(() -> new StoredFileDoesNotFoundException(id));

        if (file.getStatus() != UPLOADED) {
            throw new StoredFileIsNotReadyException(file.getId());
        }

        return queryRepository.generateTemporaryUrl(id)
                .orElseThrow(() -> new AccessDeniedException("File is not accessible"));
    }

    public StoredFileTemporaryUrl download(@Valid @NotNull Long id, @Valid @NotNull String variantKey) {
        Set<StoredFile> variants = queryRepository.fetchVariants(id);
        return variants.stream().filter(variant -> variant.getVariantKey().equals(variantKey))
                .findFirst()
                .flatMap(variant -> queryRepository.generateTemporaryUrl(variant.getId()))
                .orElseThrow(() -> new StoredFileDoesNotFoundException(variantKey));
    }

    public FetchIdResult fetchHash(@Valid @NotNull StoredFileFetchHashCommand command) {
        Long tenantId = UserLoginInfo.getCurrent().tenantId();
        StoredFile file = queryRepository.findByCharacteristicAndKey(tenantId, command.characteristics(), command.key())
                .orElseThrow(() -> new StoredFileDoesNotFoundException(command));
        return new FetchIdResult(file.getId());
    }

    public void head(Long id) {
        if (!queryRepository.existsById(id)) {
            throw new StoredFileDoesNotFoundException(id);
        }
    }

    public Page<StoredFile> panelSearch(@Valid @NotNull StoredFileSearchCriteria criteria, Pageable pageable) {
        UserLoginInfo info = UserLoginInfo.getCurrent();
        Long tenant = info.tenantId();
        criteria.setTenant(new LongFilter().setEquals(tenant));
        criteria.setAuthValues(new StringFilter().setContains(String.join(",", info.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new))));
        return queryRepository.search(criteria, pageable);
    }

    public Page<StoredFile> adminSearch(@Valid @NotNull StoredFileSearchCriteria criteria, Pageable pageable) {
        return queryRepository.search(criteria, pageable);
    }

    public boolean canAccess(Long id) {
        if (id == null) {
            throw new StoredFileDoesNotFoundException("");
        }
        StoredFile file = queryRepository.findById(id).orElseThrow(() -> new StoredFileDoesNotFoundException(id));
        UserLoginInfo info = UserLoginInfo.findCurrent().orElse(null);
        Long requestedTenantId = info == null ? null : info.tenantId();
        Long requestedUserId = info == null ? null : info.userId();
        List<String> authValues = info == null ? new ArrayList<>() : info.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return file.getAuth().canAccess(requestedTenantId, requestedUserId, authValues);
    }

    public List<Map<String, Object>> metadata(@Valid @NotNull List<String> ids) {
        return queryRepository.findMetadataByIds(ids);
    }

    public StoredFileContent fetchStream(@Valid @NotNull Long id) {
        return queryRepository.fetchContent(id).orElseThrow(() -> new StoredFileDoesNotFoundException(id));
    }
}
