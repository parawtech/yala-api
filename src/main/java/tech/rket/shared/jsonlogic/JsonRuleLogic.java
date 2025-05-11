package tech.rket.shared.jsonlogic;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = JsonLogicRuleValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonRuleLogic {
    String message() default "{jsonLogicRule.isInValid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    boolean inverse() default false;
}
