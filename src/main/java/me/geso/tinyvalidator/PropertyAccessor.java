package me.geso.tinyvalidator;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.geso.tinyvalidator.constraints.NotNull;

class PropertyAccessor {
	private static final Logger logger = LoggerFactory
			.getLogger(PropertyAccessor.class);

	private final Method readMethod;
	private final PropertyDescriptor descriptor;
	private final List<Annotation> annotations;
	private final Optional<NotNull> notNullAnnotation;
	private final Optional<Valid> validAnnotation;

	public PropertyAccessor(final Object bean,
			final PropertyDescriptor descriptor) {
		this.readMethod = descriptor.getReadMethod();
		this.readMethod.setAccessible(true);
		List<Annotation> annotations = new ArrayList<>();
		try {
			Field field = bean.getClass().getDeclaredField(
					descriptor.getName());
			if (field != null) {
				for (Annotation annotation : field.getAnnotations()) {
					annotations.add(annotation);
				}
			}
		} catch (NoSuchFieldException e) {
			// do nothing.
			logger.debug("{} doesn't have {} field: {}", bean.getClass(),
					descriptor.getName(), e.getMessage());
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
		for (Annotation annotation : readMethod.getAnnotations()) {
			annotations.add(annotation);
		}
		this.annotations = Collections.unmodifiableList(annotations);
		this.notNullAnnotation = this.buildNotNullAnnotation(annotations);
		this.validAnnotation = PropertyAccessor.findValidAnnotations(annotations);
		this.descriptor = descriptor;
	}

	// find NotNull annotation from annotation list.
	private Optional<NotNull> buildNotNullAnnotation(List<Annotation> annotations) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof NotNull) {
				return Optional.of((NotNull) annotation);
			}
		}
		return Optional.empty();
	}

	// find NotNull annotation from annotation list.
	private static Optional<Valid> findValidAnnotations(List<Annotation> annotations) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof Valid) {
				return Optional.of((Valid) annotation);
			}
		}
		return Optional.empty();
	}

	List<Annotation> getAnnotations() {
		return this.annotations;
	}

	Object get(Object object) {
		try {
			return this.readMethod.invoke(object);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	String getName() {
		return this.descriptor.getName();
	}

	Optional<NotNull> getNotNullAnnotation() {
		return this.notNullAnnotation;
	}

	Optional<Valid> getValidAnnotation() {
		return validAnnotation;
	}

}
