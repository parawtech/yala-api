package tech.rket.shared.jsonlogic;

import io.github.jamsesso.jsonlogic.ast.JsonLogicParseException;
import io.github.jamsesso.jsonlogic.ast.JsonLogicParser;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class JsonLogicRuleValidator implements ConstraintValidator<JsonRuleLogic, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            JsonLogicParser.parse(value);
            return true;
        } catch (JsonLogicParseException e) {
            context.buildConstraintViolationWithTemplate("jsonLogic.rule.isInvalid");
            return false;
        }
    }
}
