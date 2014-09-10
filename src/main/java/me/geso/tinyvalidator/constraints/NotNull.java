package me.geso.tinyvalidator.constraints;

import me.geso.tinyvalidator.ConstraintValidator;

import java.lang.annotation.*;

/**
 * The annotated element must not be null. Accepts any type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface NotNull {
    String message() default "may not be null.";

    public static class Validator implements ConstraintValidator {
        @Override
        public boolean isValid(Annotation annotation, Object value) {
            return value != null;
        }
    }
}
