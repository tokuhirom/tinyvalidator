package me.geso.tinyvalidator.constraints;

import me.geso.tinyvalidator.Constraint;
import me.geso.tinyvalidator.ConstraintValidator;
import org.apache.commons.validator.routines.EmailValidator;

import java.lang.annotation.*;

/**
 * Validate the value is a valid E-mail address.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Constraint(validatedBy = Email.Validator.class)
public @interface Email {
    public String message() default "must be valid E-mail addresss";

    public static class Validator implements ConstraintValidator {
        @Override
        public boolean isValid(Annotation annotation, Object fieldValue) {
            if (fieldValue == null) {
                return true;
            }
            if (!(fieldValue instanceof String)) {
                return false;
            }

            EmailValidator validator = EmailValidator.getInstance();
            return validator.isValid((String) fieldValue);
        }
    }
}
