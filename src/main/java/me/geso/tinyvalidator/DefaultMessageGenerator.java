package me.geso.tinyvalidator;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import lombok.SneakyThrows;

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

	@SneakyThrows
	public <T> String generateMessage(ConstraintViolation<T> violation) {
		if (generators.containsKey(violation.getAnnotation().annotationType())) {
			MessageGenerator messageGenerator = generators.get(violation
					.getAnnotation().annotationType());
			return messageGenerator.generateMessage(violation);
		}

		return violation.getRoutePath(".") + " " + violation.getMessage();
	}
}
