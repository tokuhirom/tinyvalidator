package me.geso.tinyvalidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for constraint annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Constraint {
	/**
	 * Reference for constraint validator implementation.
	 */
	public Class<? extends ConstraintValidator> validatedBy();
}
