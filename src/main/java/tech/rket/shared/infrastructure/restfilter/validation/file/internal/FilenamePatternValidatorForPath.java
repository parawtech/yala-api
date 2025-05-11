package tech.rket.shared.infrastructure.restfilter.validation.file.internal;

import tech.rket.shared.infrastructure.restfilter.validation.file.FilenamePattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.nio.file.Path;

public class FilenamePatternValidatorForPath extends FilenamePatternValidator implements ConstraintValidator<FilenamePattern, Path> {

    @Override
    public boolean isValid(Path value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Path fileName = value.getFileName();
        if (fileName == null) {
            // todo check logic to apply
            return false;
        }
        return super.isValid(fileName.toString(), context);
    }

}
