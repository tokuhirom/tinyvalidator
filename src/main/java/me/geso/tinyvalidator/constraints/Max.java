package me.geso.tinyvalidator.constraints;

import me.geso.tinyvalidator.Constraint;
import me.geso.tinyvalidator.rules.MaxConstraintValidator;

import java.lang.annotation.*;

/**
 * The annotated element must not be null. Accepts any type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Constraint(validatedBy = MaxConstraintValidator.class)

public @interface Max {

	/**
	 * @return size the element must greater than or equal to
	 */
	long value() default 0;

	String message() default "value must be <= {value}.";

}
