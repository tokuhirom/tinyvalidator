package me.geso.tinyvalidator;

import java.lang.annotation.Annotation;

/**
 * Constraint validator.
 */
public interface ConstraintValidator {
    /**
     * Validate {@code value}.
     *
     * @param annotation Annotation object for the value.
     * @param value      Target value for validation.
     * @return True if the value is valid. False otherwise.
     */
    public boolean isValid(Annotation annotation, Object value);
}
