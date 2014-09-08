package me.geso.tinyvalidator;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public class ConstraintViolation<T> {
	private final T object;
	private final Annotation annotation;
	private final List<String> route;
	private final Object fieldValue;

	public ConstraintViolation(T object, Object fieldValue, Annotation annotation, List<String> route) {
		this.object = object;
		this.fieldValue = fieldValue;
		this.annotation = annotation;
		this.route = route;
	}

	public T getObject() {
		return object;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	public List<String> getRoute() {
		return route;
	}

	public String getRoutePath(String pathSeparator) {
		return route.stream().collect(Collectors.joining(pathSeparator));
	}

	public Object getFieldValue() {
		return fieldValue;
	}
}
