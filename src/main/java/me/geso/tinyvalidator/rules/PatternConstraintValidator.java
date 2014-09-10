package me.geso.tinyvalidator.rules;

import java.lang.annotation.Annotation;
import java.util.regex.Matcher;

import me.geso.tinyvalidator.ConstraintValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.geso.tinyvalidator.constraints.Pattern;

public class PatternConstraintValidator implements ConstraintValidator {
	private static Logger logger = LoggerFactory.getLogger(PatternConstraintValidator.class);

	@Override
	public boolean isValid(Annotation annotation,
                           Object fieldValue) {
		if (fieldValue instanceof String) {
			Pattern pattern = (Pattern) annotation;
			java.util.regex.Pattern compiled = java.util.regex.Pattern
					.compile(pattern.regexp(), pattern.flags());
			Matcher matcher = compiled.matcher((String) fieldValue);
			return matcher.find();
		} else {
			logger.warn("You shouldn't set @Pattern for non String field/getter.");
			return false;
		}
	}

}
