package me.geso.tinyvalidator.rules;

import java.lang.annotation.Annotation;
import java.util.regex.Matcher;

import lombok.extern.slf4j.Slf4j;
import me.geso.tinyvalidator.ConstraintValidator;
import me.geso.tinyvalidator.constraints.Pattern;

@Slf4j
public class PatternConstraintValidator implements ConstraintValidator {
	@Override
	public boolean isValid(Annotation annotation,
			Object fieldValue) {
		if (fieldValue instanceof String) {
			Pattern pattern = (Pattern)annotation;
			java.util.regex.Pattern compiled = java.util.regex.Pattern
				.compile(pattern.regexp(), pattern.flags());
			Matcher matcher = compiled.matcher((String)fieldValue);
			return matcher.find();
		} else {
			log.warn("You shouldn't set @Pattern for non String field/getter.");
			return false;
		}
	}

}
