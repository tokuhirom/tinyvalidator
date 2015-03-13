package me.geso.tinyvalidator.constraints;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Matcher;

import me.geso.tinyvalidator.Constraint;
import me.geso.tinyvalidator.ConstraintValidator;

/**
 * Validate the value as a valid HTTP URL.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Constraint(validatedBy = HttpUrl.Validator.class)
public @interface HttpUrl {
	String message() default "must be valid HTTP URL";

	public static class Validator implements ConstraintValidator {
		private static final java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\As?https?://[-_.!~*'()a-zA-Z0-9;/?:@&=+$,%#]+\\z");

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
