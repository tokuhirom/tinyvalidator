package me.geso.tinyvalidator;

import java.lang.annotation.Annotation;
import java.util.List;

public interface Rule {
	public boolean validate(Object root, Object target, List<String> route,
			String name, Annotation annotation, Object fieldValue);
}
