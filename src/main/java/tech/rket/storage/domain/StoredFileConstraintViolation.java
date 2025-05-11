package tech.rket.storage.domain;

import tech.rket.shared.core.domain.result.DomainConstraintViolation;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum StoredFileConstraintViolation implements DomainConstraintViolation {
    METADATA_DOES_NOT_EXISTS("Metadata does not exist."),
    STORED_FILE_IS_SENT_FOR_UPLOAD_ALREADY("File is sent for upload already."),
    STORED_FILE_IS_MARKED_FOR_DELETED_ALREADY("Stored file is marked for deleted already."),
    STORED_FILE_DOES_NOT_SENT_FOR_UPLOAD("File is not sent for upload."),
    STORED_FILE_REGISTERED_MIME_TYPE_IS_DIFFERENT("Registered Mime type is different"),
    FILE_CANNOT_BE_PARENT_OF_ITSELF("File cannot be parent of itself"),
    FILE_CANNOT_BE_CHILD_OF_ITS_CHILD("File cannot be child of its child"),
    ;
    private final String message;

    StoredFileConstraintViolation(String s) {
        message = s;
    }

    @Override
    public String code() {
        return name();
    }

    @Override
    public String message() {
        return message;
    }


    public static class StoredFileUploadViolation implements DomainConstraintViolation.Parameterized {
        private final Long id;
        private final String message;
        @Getter
        private final List<Object> parameters = new ArrayList<>();

        public StoredFileUploadViolation(Long id, String message) {
            this.id = id;
            this.message = message;
            this.parameters.add(id);
        }

        @Override
        public String code() {
            return "STORED_FILE_CONTENT_UPLOAD_VIOLATION";
        }

        @Override
        public String message() {
            return String.format("File %d can not be uploaded due to %s.", id, message);
        }
    }

}
