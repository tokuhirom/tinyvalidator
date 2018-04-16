package me.geso.tinyvalidator.rules;

import me.geso.tinyvalidator.ConstraintValidator;
import me.geso.tinyvalidator.constraints.Max;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;

public class MaxConstraintValidator implements ConstraintValidator {
	private static final Logger logger = LoggerFactory.getLogger(MaxConstraintValidator.class);

	@Override
	public boolean isValid(Annotation annotation,
						   Object fieldValue) {
		Max maxAnnotation = (Max) annotation;

		long value = getValue(fieldValue);

		if (value > maxAnnotation.value()) {
			if (logger.isDebugEnabled()) {
				logger.debug("MaxConstraintValidator: {} > {}", value, maxAnnotation.value());
			}
			return false;
		}
		return true;
	}

	private long getValue(Object fieldValue) {
		if (fieldValue instanceof Long){
			return ((long) fieldValue);
		} else if (fieldValue instanceof Integer) {
			return ((long) ((Integer) fieldValue).intValue());
		} else if (fieldValue == null) {
			throw new RuntimeException("me.geso.tinyvalidator.rules.MaxConstraintValidator: The value is null");
		} else {
			throw new RuntimeException("me.geso.tinyvalidator.rules.MaxConstraintValidator can't get a value from: "
					+ fieldValue.getClass());
		}
	}

}
