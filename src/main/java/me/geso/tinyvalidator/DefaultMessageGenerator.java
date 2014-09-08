package me.geso.tinyvalidator;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import me.geso.tinyvalidator.constraints.NotNull;
import me.geso.tinyvalidator.constraints.Pattern;
import me.geso.tinyvalidator.constraints.Size;

public class DefaultMessageGenerator implements MessageGenerator {
	private final Map<Class<? extends Annotation>, MessageGenerator> generators = new HashMap<>();
	
	public void addMessageGenerator(Class<? extends Annotation> annotationClass, MessageGenerator messageGenerator) {
		this.generators.put(annotationClass, messageGenerator);
	}

	public <T> String generateMessage(ConstraintViolation<T> violation) {
		if (generators.containsKey(violation.getAnnotation().annotationType())) {
			MessageGenerator messageGenerator = generators.get(violation.getAnnotation().annotationType());
			return messageGenerator.generateMessage(violation);
		}

		if (violation.getAnnotation().annotationType() == NotNull.class) {
			return String.format("You should fill %s.", violation.getRoutePath("."));
		} else if (violation.getAnnotation().annotationType() == Pattern.class) {
			Pattern pattern = (Pattern)violation.getAnnotation();
			return String.format("%s does not matches %s.", violation.getRoutePath("."), pattern.regexp());
		} else if (violation.getAnnotation().annotationType() == Size.class) {
			Size size = (Size)violation.getAnnotation();
			return String.format("%s: %s < %s < %s", violation.getRoutePath("."), size.min(), violation.getFieldValue(), size.max());
		} else {
			throw new RuntimeException("Unknown message type: " + violation.getAnnotation());
		}
	}
}
