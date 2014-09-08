package me.geso.tinyvalidator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

class FieldAccessor implements Accessor {
	
	private final Field field;

	public FieldAccessor(Field field) {
		this.field = field;
		this.field.setAccessible(true);
	}

	@Override
	public Annotation[] getAnnotations() {
		return this.field.getAnnotations();
	}

	@Override
	public Object get(Object object) {
		try {
			return this.field.get(object);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getName() {
		return this.field.getName();
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return this.field.getAnnotation(annotationClass);
	}

}
