package tech.rket.storage.presentation.rest;

import tech.rket.storage.application.StorageManipulationService;
import tech.rket.storage.application.StorageQueryService;
import tech.rket.storage.application.command.StoredFileContentPatchCommand;
import tech.rket.storage.application.command.StoredFileFetchHashCommand;
import tech.rket.storage.application.command.StoredFileReserveCommand;
import tech.rket.storage.application.command.StoredFileUploadCommand;
import tech.rket.storage.application.result.FetchIdResult;
import tech.rket.storage.application.result.PatchContentResult;
import tech.rket.storage.application.result.ReserveResult;
import tech.rket.storage.application.result.UploadResult;
import tech.rket.storage.domain.StoredFile;
import tech.rket.storage.domain.entity.StoredFileContent;
import tech.rket.storage.domain.entity.StoredFileTemporaryUrl;
import tech.rket.storage.domain.query.StoredFileSearchCriteria;
import tech.rket.storage.domain.value_object.MimeType;
import tech.rket.storage.domain.value_object.StoredFileAuthType;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("storage")
@RequiredArgsConstructor
public class StorageController {
    private final StorageManipulationService manipulationService;
    private final StorageQueryService queryService;

    @GetMapping("api/{id}")
    @PreAuthorize("@storageQueryService.canAccess(#id)")
    public void redirect(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        var temporaryUrl = queryService.download(id);
        redirect(temporaryUrl, response);
    }

    @GetMapping("api/{id}/{variantKey}")
    @PreAuthorize("@storageQueryService.canAccess(#id)")
    public void redirectVariant(@PathVariable("id") Long id,
                                @PathVariable("variantKey") String variantKey,
                                HttpServletResponse response) throws IOException {
        var temporaryUrl = queryService.download(id, variantKey);
        redirect(temporaryUrl, response);
    }

    @PostMapping("panel/{characteristics}/{key}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UploadResult> upload(@PathVariable("characteristics") String characteristics,
                                               @Valid @NotNull @RequestBody MultipartFile file,
                                               @PathVariable("key") String key,
                                               @Valid @NotNull @RequestParam("authType") StoredFileAuthType authType,
                                               @RequestParam(value = "authValues", required = false) List<String> authValues) {
        var result = manipulationService.upload(new StoredFileUploadCommand(characteristics, key, authType, authValues, file));
        return ResponseEntity.ok(result);
    }

    @PostMapping("panel/{characteristics}/{key}/reservation")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReserveResult> reservation(@PathVariable("characteristics") String characteristics,
                                                     @PathVariable("key") String key,
                                                     @Valid @NotNull @RequestParam("authType") StoredFileAuthType authType,
                                                     @Valid @NotNull @RequestParam("mimeType") MimeType mimeType,
                                                     @RequestParam(value = "authValues", required = false) List<String> authValues) {
        var result = manipulationService.reservation(new StoredFileReserveCommand(characteristics, key, authType, authValues, mimeType));
        return ResponseEntity.ok(result);
    }

    @PatchMapping("panel/{id}/file")
    @PreAuthorize("isAuthenticated()  && @storageQueryService.canAccess(#id)")
    public ResponseEntity<PatchContentResult> patchContent(@PathVariable("id") Long id, @Valid @NotNull @RequestBody MultipartFile file) {
        var result = manipulationService.patchContent(new StoredFileContentPatchCommand(id, file));
        return ResponseEntity.ok(result);
    }

    @GetMapping("panel/{characteristics}/{key}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FetchIdResult> revokeId(@PathVariable("characteristics") String characteristics, @PathVariable("key") String key) {
        return ResponseEntity.ok(queryService.fetchHash(new StoredFileFetchHashCommand(characteristics, key)));
    }

    @RequestMapping(value = "panel/{id}", method = RequestMethod.HEAD)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> head(@PathVariable("id") Long id) {
        queryService.head(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("panel/{id}")
    @PreAuthorize("isAuthenticated()  && @storageQueryService.canAccess(#id)")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        manipulationService.delete(id);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("panel/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<StoredFile>> panelSearch(@Valid @NotNull @RequestBody StoredFileSearchCriteria criteria, Pageable pageable) {
        return ResponseEntity.ok(queryService.panelSearch(criteria, pageable));
    }

    @PostMapping("admin/search")
    @PreAuthorize("isAdmin()")
    public ResponseEntity<Page<StoredFile>> adminSearch(@Valid @NotNull @RequestBody StoredFileSearchCriteria criteria, Pageable pageable) {
        return ResponseEntity.ok(queryService.adminSearch(criteria, pageable));
    }

    @DeleteMapping("admin/{id}")
    @PreAuthorize("isAdmin()")
    public ResponseEntity<Page<StoredFile>> adminDelete(@PathVariable("id") Long id) {
        manipulationService.delete(id);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("admin/{id}/buffer")
    @PreAuthorize("isAdmin()")
    public void adminBuffer(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        StoredFileContent storedFileContent = queryService.fetchStream(id);
        storedFileContent.stream().transferTo(response.getOutputStream());
        response.setContentType(storedFileContent.mimeType().getMimeType());
        response.setContentLength(storedFileContent.size().intValue());
    }

    @PostMapping("internal/metadata")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Map<String, Object>>> bulkData(@Valid @NotNull List<String> ids) {
        // EXPLAIN: for bulk metadata using in marketing mostly
        List<Map<String, Object>> result = queryService.metadata(ids);
        return ResponseEntity.ok(result);
    }

    void redirect(StoredFileTemporaryUrl temporaryUrl, HttpServletResponse response) throws IOException {
        long remainingTimeMillis = temporaryUrl.expire() == null ? 0 : temporaryUrl.expire().getTime() - System.currentTimeMillis();
        if (remainingTimeMillis > 0) {
            response.setHeader("Cache-Control", "private, max-age=" + (remainingTimeMillis / 1000));
            response.setDateHeader("Expires", temporaryUrl.expire().getTime());
        } else {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
        }
        response.sendRedirect(temporaryUrl.temporaryUrl());
    }
}
