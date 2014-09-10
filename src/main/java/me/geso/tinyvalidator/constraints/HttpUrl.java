package me.geso.tinyvalidator.constraints;

import me.geso.tinyvalidator.Constraint;
import me.geso.tinyvalidator.ConstraintValidator;

import java.lang.annotation.*;
import java.util.regex.Matcher;

/**
 * Validate the value as a valid HTTP URL.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Constraint(validatedBy = HttpUrl.Validator.class)
public @interface HttpUrl {
    String message() default "must be valid HTTP URL";

    public static class Validator implements ConstraintValidator {
        private static final java.util.regex.Pattern pattern
                = java.util.regex.Pattern.compile("\\As?https?://[-_.!~*'()a-zA-Z0-9;/?:@&=+$,%#]+\\z");

        @Override
        public boolean isValid(Annotation annotation, Object fieldValue) {
            if (fieldValue == null) {
                return true;
            }

            final Matcher matcher = pattern.matcher(fieldValue.toString());
            return matcher.matches();
        }
    }
}
