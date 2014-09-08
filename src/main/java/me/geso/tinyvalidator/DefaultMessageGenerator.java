package me.geso.tinyvalidator;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import me.geso.tinyvalidator.constraints.NotNull;
import me.geso.tinyvalidator.constraints.Pattern;
import me.geso.tinyvalidator.constraints.Size;

public class DefaultMessageGenerator implements MessageGenerator {
	@Override
	public String toString() {
		return "DefaultMessageGenerator [generators=" + generators + "]";
	}

	private final Map<Class<? extends Annotation>, MessageGenerator> generators = new HashMap<>();

	public void addMessageGenerator(
			Class<? extends Annotation> annotationClass,
			MessageGenerator messageGenerator) {
		this.generators.put(annotationClass, messageGenerator);
	}

	public <T> String generateMessage(ConstraintViolation<T> violation) {
		if (generators.containsKey(violation.getAnnotation().annotationType())) {
			MessageGenerator messageGenerator = generators.get(violation
					.getAnnotation().annotationType());
			return messageGenerator.generateMessage(violation);
		}

		// ref. https://svn.apache.org/repos/asf/tapestry/tapestry5/trunk/tapestry-beanvalidator/src/test/resources/ValidationMessages_en.properties
		if (violation.getAnnotation().annotationType() == NotNull.class) {
			return String.format("%s may not be null.",
					violation.getRoutePath("."));
		} else if (violation.getAnnotation().annotationType() == Pattern.class) {
			Pattern pattern = (Pattern) violation.getAnnotation();
			return String.format("%s must match %s.",
					violation.getRoutePath("."), pattern.regexp());
		} else if (violation.getAnnotation().annotationType() == Size.class) {
			Size size = (Size) violation.getAnnotation();
			return String.format("%s size must be between %d and %d. But %d.",
					violation.getRoutePath("."), size.min(), size.max(),
					violation.getFieldValue());
		} else {
			throw new RuntimeException("Unknown message type: "
					+ violation.getAnnotation());
		}
	}
}
