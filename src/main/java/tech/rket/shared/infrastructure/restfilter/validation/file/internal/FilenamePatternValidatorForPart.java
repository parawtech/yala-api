package tech.rket.shared.infrastructure.restfilter.validation.file.internal;

import org.apache.commons.lang3.StringUtils;
import tech.rket.shared.infrastructure.restfilter.validation.file.FilenamePattern;

import jakarta.servlet.http.Part;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FilenamePatternValidatorForPart extends FilenamePatternValidator implements ConstraintValidator<FilenamePattern, Part> {

    @Override
    public boolean isValid(Part value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String fileName = value.getSubmittedFileName();
        if (StringUtils.isBlank(fileName)) {
            // todo check logic to apply
            return false;
        }
        return super.isValid(fileName, context);
    }

}
