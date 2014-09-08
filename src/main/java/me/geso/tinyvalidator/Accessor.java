package me.geso.tinyvalidator;

import java.lang.annotation.Annotation;

/**
 * Implementations:
 * <ul>
 * <li>{@code PropertyAccessor}</li>
 * <li>{@code FieldAccessor}</li>
 * </ul>
 * 
 * @author tokuhirom
 *
 */
interface Accessor {
	public Annotation[] getAnnotations();

	public Object get(Object object);

	public String getName();

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass);
}
