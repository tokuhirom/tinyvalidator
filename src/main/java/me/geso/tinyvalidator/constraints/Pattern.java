package me.geso.tinyvalidator.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated String must match the following regular expression. The regular
 * expression follows the Java regular expression conventions see Pattern.
 * Accepts String. null elements are considered valid.
 * 
 * @author tokuhirom
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Pattern {
	String regexp();
}
