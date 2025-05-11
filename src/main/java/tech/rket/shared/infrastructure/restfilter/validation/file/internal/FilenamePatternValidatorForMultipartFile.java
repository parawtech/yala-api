package tech.rket.shared.infrastructure.restfilter.validation.file.internal;

import org.apache.commons.lang3.StringUtils;
import tech.rket.shared.infrastructure.restfilter.validation.file.FilenamePattern;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FilenamePatternValidatorForMultipartFile extends FilenamePatternValidator implements ConstraintValidator<FilenamePattern, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String fileName = value.getOriginalFilename();
        if (StringUtils.isBlank(fileName)) {
            // todo check logic to apply
            return false;
        }
        return super.isValid(fileName, context);
    }

}
