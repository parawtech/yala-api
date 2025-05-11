package tech.rket.storage.application;

import tech.rket.shared.infrastructure.persistence.shared.DomainConstraintViolationException;
import tech.rket.storage.application.command.StoredFileContentPatchCommand;
import tech.rket.storage.application.command.StoredFileReserveCommand;
import tech.rket.storage.application.command.StoredFileUploadCommand;
import tech.rket.storage.application.exception.StoredFileDoesAlreadyExistsException;
import tech.rket.storage.application.exception.StoredFileDoesNotFoundException;
import tech.rket.storage.application.exception.StoredFileInputStreamException;
import tech.rket.storage.application.exception.StoredFileNotSupportedException;
import tech.rket.storage.application.result.PatchContentResult;
import tech.rket.storage.application.result.ReserveResult;
import tech.rket.storage.application.result.UploadResult;
import tech.rket.storage.domain.StoredFile;
import tech.rket.storage.domain.StoredFileEventPublisher;
import tech.rket.storage.domain.StoredFileRepository;
import tech.rket.storage.domain.command.StoreFileInstantiate;
import tech.rket.storage.domain.command.StoredFileSendForUpload;
import tech.rket.storage.domain.value_object.MimeType;
import tech.rket.storage.domain.value_object.StoredFileAuth;
import tech.rket.storage.infrastructure.InputStreamInMemoryStorage;
import tech.rket.shared.infrastructure.auth.UserLoginInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class StorageManipulationService {
    private final StoredFileRepository repository;
    private final StoredFileEventPublisher eventPublisher;
    private final InputStreamInMemoryStorage inputStreamInMemoryStorage;

    public UploadResult upload(@Valid @NotNull StoredFileUploadCommand command) {
        MimeType mimeType = MimeType.ofContentType(command.file().getContentType())
                .orElseThrow(() -> new StoredFileNotSupportedException(command.file().getContentType()));

        ReserveResult reserveResult = reservation(new StoredFileReserveCommand(
                command.characteristic(),
                command.key(),
                command.authType(),
                command.authValues(),
                mimeType
        ));
        PatchContentResult patchResult = patchContent(new StoredFileContentPatchCommand(
                reserveResult.id(),
                command.file()
        ));
        return new UploadResult(reserveResult.id(), reserveResult.characteristic(), reserveResult.key(), patchResult.size(), reserveResult.mimeType(), null);
    }

    public void delete(Long id) {
        StoredFile file = repository.findById(id)
                .orElseThrow(() -> new StoredFileDoesNotFoundException(id));
        file.markForDelete()
                .throwIfFailure(DomainConstraintViolationException::new);
        repository.save(file);
        eventPublisher.publish(file);
    }

    public ReserveResult reservation(StoredFileReserveCommand command) {
        UserLoginInfo loginInfo = UserLoginInfo.getCurrent();
        Long id = repository.generateID();
        StoredFileAuth owner = new StoredFileAuth(command.authType(), loginInfo.userId(), loginInfo.tenantId(), command.authValues());
        StoreFileInstantiate instantiate = new StoreFileInstantiate(
                id,
                loginInfo.tenantId(),
                loginInfo.userId(),
                command.characteristic(),
                command.key(),
                -1L,
                command.mimeType(),
                owner,
                null,
                null);

        StoredFile file = StoredFile.of(instantiate)
                .throwIfFailure(DomainConstraintViolationException::new)
                .value();
        try {
            repository.save(file);
        } catch (DataIntegrityViolationException e) {
            throw new StoredFileDoesAlreadyExistsException(command);
        }
        eventPublisher.publish(file);
        return new ReserveResult(file.getId(), file.getCharacteristic(), file.getKey(), file.getMimeType());
    }

    public PatchContentResult patchContent(@Valid @NotNull StoredFileContentPatchCommand command) {
        StoredFile file = repository.findById(command.id()).orElseThrow(() -> new StoredFileDoesNotFoundException(command.id()));
        MimeType mimeType = MimeType.ofContentType(command.file().getContentType())
                .orElseThrow(() -> new StoredFileNotSupportedException(command.file().getContentType()));
        InputStream stream;
        try {
            stream = command.file().getInputStream();
        } catch (IOException e) {
            throw new StoredFileInputStreamException(e);
        }

        file.sentForUpload(new StoredFileSendForUpload(command.file().getSize(), mimeType))
                .throwIfFailure(DomainConstraintViolationException::new);

        inputStreamInMemoryStorage.save(file.getId(), stream);
        repository.save(file);
        eventPublisher.publish(file);
        return new PatchContentResult(file.getId(), command.file().getSize(), file.getStatus());
    }
}
