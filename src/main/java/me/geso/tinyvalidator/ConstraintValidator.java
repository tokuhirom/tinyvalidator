package me.geso.tinyvalidator;

import java.lang.annotation.Annotation;
import java.util.List;

public interface ConstraintValidator {
	public boolean isValid(Object root, Object target, List<String> route,
                           String name, Annotation annotation, Object fieldValue);
}
