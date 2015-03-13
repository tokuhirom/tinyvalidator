package me.geso.tinyvalidator.constraints;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.validator.routines.EmailValidator;

import me.geso.tinyvalidator.Constraint;
import me.geso.tinyvalidator.ConstraintValidator;

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
			return validator.isValid((String)fieldValue);
		}
	}
}
