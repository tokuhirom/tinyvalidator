package me.geso.tinyvalidator;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class PropertyAccessor implements Accessor {
	
	private final Method readMethod;
	private final PropertyDescriptor descriptor;

	public PropertyAccessor(final PropertyDescriptor descriptor) {
		this.readMethod = descriptor.getReadMethod();
		this.readMethod.setAccessible(true);
		this.descriptor = descriptor;
	}
	
	@Override
	public Annotation[] getAnnotations() {
		return this.readMethod.getAnnotations();
	}

	@Override
	public Object get(Object object) {
		try {
			return this.readMethod.invoke(object);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getName() {
		return this.descriptor.getName();
	}

	@Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return this.readMethod.getAnnotation(annotationClass);
	}

}
