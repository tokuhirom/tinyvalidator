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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        ValidationContext<T> context = new ValidationContext<>(bean);
        Node node = new Node();
        this.doValidate(bean, context, node);
        return context.getViolations();
    }

    private <T> void doValidate(Object target,
                                ValidationContext<T> context,
                                Node node) {
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "Checking {}", target.getClass());
        }

        context.registerSeen(target);

        PropertyAccessor[] accessors = this.getAccessors(target);
        for (PropertyAccessor accessor : accessors) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "Checking root: {}, target: {} descriptor: {}",
                        context.getRootObject(), target.getClass(),
                        accessor.getName());
            }

            this.validateField(target, context, accessor, node);
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
    private <T> void validateField(Object target,
                                   ValidationContext<T> context,
                                   PropertyAccessor accessor,
                                   Node node) {
        String name = accessor.getName();
        Object fieldValue = accessor.get(target);

        Optional<NotNull> notNullAnnotation = accessor.getNotNullAnnotation();
        if (logger.isDebugEnabled()) {
            logger.debug("{}.{}'s notNullAnnotation: {}", context.getRoute(),
                    accessor.getName(), notNullAnnotation);
        }
        if (fieldValue == null) {
            if (notNullAnnotation.isPresent()) {
                logger.debug("{} is null", context.getRoute());
                context.addViolation(
                        notNullAnnotation.get(),
                        fieldValue,
                        node.child(name)
                );
            }
            return;
        }

        for (Annotation annotation : accessor.getAnnotations()) {
            final Constraint constraint = annotation.annotationType().getAnnotation(Constraint.class);
            if (constraint == null) {
                logger.debug(
                        "{} doesn't have a @Constraint annotation: {}",
                        annotation.annotationType(), annotation.annotationType().getAnnotations()
                );
                continue;
            }
            if (!this.validateByAnnotation(constraint, annotation, fieldValue)) {
                context.addViolation(
                        annotation,
                        fieldValue,
                        node.child(name)
                );
            }
        }

        // Checking child by recursion.
        if (fieldValue != null && accessor.getValidAnnotation().isPresent()) {
            if (!context.isSeen(fieldValue)) {
                this.doValidate(fieldValue, context, node.child(name));
            }
        }
    }

    /**
     * Validate value by annotation.
     *
     * @param constraint
     * @param annotation
     * @param value
     * @return
     */
    @SneakyThrows
    public boolean validateByAnnotation(Constraint constraint,
                                        Annotation annotation,
                                        Object value) {
        final Class<? extends ConstraintValidator> constraintValidatorClass = constraint.validatedBy();
        final ConstraintValidator constraintValidator = constraintValidatorClass.newInstance();
        return constraintValidator.isValid(annotation, value);
    }
}
