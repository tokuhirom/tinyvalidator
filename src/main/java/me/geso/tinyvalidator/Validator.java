package me.geso.tinyvalidator;

import lombok.SneakyThrows;
import me.geso.tinyvalidator.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Validator {
    private static Logger logger = LoggerFactory.getLogger(Validator.class);
    private static Map<Class<?>, PropertyAccessor[]> accessorCache = new ConcurrentHashMap<>();

    public Validator() {
    }

    /**
     * Validate bean.
     *
     * @param bean
     * @return return violations.
     */
    public <T> List<ConstraintViolation<T>> validate(T bean) {
        List<ConstraintViolation<T>> violations = new ArrayList<>();
        Set<Object> seen = new HashSet<>();
        doValidate(bean, bean, violations, new ArrayList<String>(), seen);
        return violations;
    }

    private <T> void doValidate(T root, Object target,
                                List<ConstraintViolation<T>> violations,
                                List<String> route,
                                Set<Object> seen) {
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "Checking {}", target.getClass());
        }

        seen.add(target);

        PropertyAccessor[] accessors = this.getAccessors(target);
        for (PropertyAccessor accessor : accessors) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "Checking root: {}, target: {} descriptor: {}",
                        root.getClass(), target.getClass(),
                        accessor.getName());
            }

            this.validateField(root, target, violations,
                    route, seen, accessor);
        }
    }

    private PropertyAccessor[] getAccessors(Object bean) {
        PropertyAccessor[] accessors = accessorCache.get(bean.getClass());
        if (accessors == null) {
            List<PropertyAccessor> accessorList = new ArrayList<>();
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
                for (PropertyDescriptor descriptor : beanInfo
                        .getPropertyDescriptors()) {
                    if ("class".equals(descriptor.getName())
                            || "classLoader".equals(descriptor.getName())) {
                        continue;
                    }
                    PropertyAccessor accessor = new PropertyAccessor(bean,
                            descriptor);
                    if (accessor.getAnnotations().size() > 0) {
                        accessorList.add(accessor);
                    }
                }
            } catch (IntrospectionException e) {
                throw new RuntimeException(e);
            }
            accessors = accessorList.stream().toArray(PropertyAccessor[]::new);
            accessorCache.put(bean.getClass(), accessors);
        }
        return accessors;
    }

    @SneakyThrows
    private <T> void validateField(T root, Object target,
                                   List<ConstraintViolation<T>> violations, List<String> route,
                                   Set<Object> seen, PropertyAccessor accessor) {
        String name = accessor.getName();
        Object fieldValue = accessor.get(target);

        Optional<NotNull> notNullAnnotation = accessor.getNotNullAnnotation();
        if (logger.isDebugEnabled()) {
            logger.debug("{}.{}'s notNullAnnotation: {}", route,
                    accessor.getName(), notNullAnnotation);
        }
        if (notNullAnnotation.isPresent()) {
            if (fieldValue == null) {
                logger.debug("{} is null", route);
                List<String> currentRoute = new ArrayList<>(route);
                currentRoute.add(name);
                violations.add(new ConstraintViolation<T>(root, target,
                        notNullAnnotation.get(),
                        currentRoute));
                return;
            }
        }

        for (Annotation annotation : accessor.getAnnotations()) {
            final Constraint constraint = annotation.annotationType().getAnnotation(Constraint.class);
            if (constraint == null) {
                throw new RuntimeException(
                        String.format("%s doesn't have a @Constraint annotation.",
                                annotation.annotationType())
                );
            }
            final Class<? extends ConstraintValidator> constraintValidatorClass = constraint.validatedBy();
            final ConstraintValidator constraintValidator = constraintValidatorClass.newInstance();
            if (!constraintValidator.validate(root, target, route, name, annotation,
                    fieldValue)) {
                List<String> currentRoute = new ArrayList<>(route);
                currentRoute.add(name);
                violations.add(new ConstraintViolation<T>(root, target,
                        annotation,
                        currentRoute));
            }
        }

        // Checking child by recursion.
        if (fieldValue != null && accessor.getValidAnnotation().isPresent()) {
            if (!seen.contains(fieldValue)) {
                List<String> currentRoute = new ArrayList<>(route);
                currentRoute.add(name);
                doValidate(root, fieldValue, violations, route, seen);
            }
        }
    }

}
