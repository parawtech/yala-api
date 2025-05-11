package tech.rket.shared.infrastructure.model.id;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@IdGeneratorType(JIDGenerator.class)
@Retention(RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface JID {
    short group() default 0;

    short type() default 0;

    String expType() default "";

    String expGroup() default "";
}
