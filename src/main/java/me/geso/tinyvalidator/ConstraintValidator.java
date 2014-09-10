package me.geso.tinyvalidator;

import java.lang.annotation.Annotation;

public interface ConstraintValidator {
    public boolean isValid(Annotation annotation, Object fieldValue);
}
