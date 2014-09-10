package me.geso.tinyvalidator.rules;

import me.geso.tinyvalidator.ConstraintValidator;
import me.geso.tinyvalidator.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

public class SizeConstraintValidator implements ConstraintValidator {
    private static final Logger logger = LoggerFactory.getLogger(SizeConstraintValidator.class);

    @Override
    public boolean isValid(Annotation annotation,
                           Object fieldValue) {
        Size sizeAnnotation = (Size) annotation;
        int size = getSize(fieldValue);

        if (size > sizeAnnotation.max()) {
            if (logger.isDebugEnabled()) {
                logger.debug("SizeConstraintValidator: {} > {}", size, sizeAnnotation.max());
            }
            return false;
        } else if (size < sizeAnnotation.min()) {
            if (logger.isDebugEnabled()) {
                logger.debug("SizeConstraintValidator: {} < {}", size, sizeAnnotation.min());
            }
            return false;
        }
        return true;
    }

    private int getSize(Object fieldValue) {
        if (fieldValue instanceof CharSequence) {
            return ((CharSequence) fieldValue).length();
        } else if (fieldValue instanceof Collection) {
            return ((Collection<?>) fieldValue).size();
        } else if (fieldValue instanceof Map) {
            return ((Map<?, ?>) fieldValue).size();
        } else if (fieldValue instanceof Object[]) {
            return ((Object[]) fieldValue).length;
        } else if (fieldValue instanceof boolean[]) {
            return ((boolean[]) fieldValue).length;
        } else if (fieldValue instanceof byte[]) {
            return ((byte[]) fieldValue).length;
        } else if (fieldValue instanceof char[]) {
            return ((char[]) fieldValue).length;
        } else if (fieldValue instanceof double[]) {
            return ((double[]) fieldValue).length;
        } else if (fieldValue instanceof float[]) {
            return ((float[]) fieldValue).length;
        } else if (fieldValue instanceof int[]) {
            return ((int[]) fieldValue).length;
        } else if (fieldValue instanceof long[]) {
            return ((long[]) fieldValue).length;
        } else if (fieldValue instanceof short[]) {
            return ((short[]) fieldValue).length;
        } else if (fieldValue == null) {
            throw new RuntimeException("me.geso.tinyvalidator.rules.SizeConstraintValidator: The value is null");
        } else {
            throw new RuntimeException("me.geso.tinyvalidator.rules.SizeConstraintValidator can't get a size from: " + fieldValue.getClass());
        }
    }

}
