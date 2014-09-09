package me.geso.tinyvalidator.rules;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import me.geso.tinyvalidator.ConstraintValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.geso.tinyvalidator.constraints.Size;

public class SizeConstraintValidator implements ConstraintValidator {
	private static final Logger logger = LoggerFactory.getLogger(SizeConstraintValidator.class);

	@Override
	public boolean isValid(Object root, Object target, List<String> route,
                           String name, Annotation annotation, Object fieldValue) {
		Size sizeAnnotation = (Size) annotation;
		int size = getSize(fieldValue);

		if (size > sizeAnnotation.max()) {
			if (logger.isDebugEnabled()){
				logger.debug("SizeRule: {} > {}", size, sizeAnnotation.max());
			}
			return false;
		} else if (size < sizeAnnotation.min()) {
			if (logger.isDebugEnabled()){
				logger.debug("SizeRule: {} < {}", size, sizeAnnotation.min());
			}
			return false;
		}
		return true;
	}
	
	private int getSize(Object fieldValue) {
		if (fieldValue instanceof String) {
			return ((String) fieldValue).length();
		} else if (fieldValue instanceof Map) {
			return ((Map<?, ?>) fieldValue).size();
		} else {
			throw new RuntimeException("me.geso.tinyvalidator.rules.SizeRule can't get a size from: " + fieldValue.getClass());
		}
	}

}
