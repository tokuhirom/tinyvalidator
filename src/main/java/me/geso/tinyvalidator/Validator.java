package me.geso.tinyvalidator;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import lombok.SneakyThrows;
import me.geso.tinyvalidator.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public <T> List<ConstraintViolation> validate(T bean) {
		ValidationContext context = new ValidationContext();
		Node node = new Node();
		this.doValidate(bean, context, node);
		return context.getViolations();
	}

	private <T> void doValidate(Object target,
			ValidationContext context,
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
						"Checking target: {} descriptor: {}",
						target.getClass(),
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
			ValidationContext context,
			PropertyAccessor accessor,
			Node node) {
		String name = accessor.getName();
		Object fieldValue = accessor.get(target);

		Optional<NotNull> notNullAnnotation = accessor.getNotNullAnnotation();
		if (logger.isDebugEnabled()) {
			logger.debug("{}.{}'s notNullAnnotation: {}", node.toString(),
					accessor.getName(), notNullAnnotation);
		}
		if (fieldValue == null) {
			if (notNullAnnotation.isPresent()) {
				logger.debug("{} is null", node.toString());
				final ConstraintViolation constraintViolation = new ConstraintViolation(
						fieldValue, notNullAnnotation.get(), node.child(name)
								.toString());
				context.addViolation(constraintViolation);
			}
			return;
		}

		for (Annotation annotation : accessor.getAnnotations()) {
			final Optional<ConstraintViolation> constraintViolationOptional = this
					.validateByAnnotation(annotation, node.child(name)
							.toString(), fieldValue);
			if (constraintViolationOptional.isPresent()) {
				context.addViolation(constraintViolationOptional.get());
			}
		}

		// Checking child by recursion.
		if (fieldValue != null && accessor.getValidAnnotation().isPresent()) {
			if (!context.isSeen(fieldValue)) {
				if (fieldValue instanceof Collection) {
					int i = 0;
					for (Object item : (Collection<?>) fieldValue) {
						this.doValidate(item, context,
								node.child(name).child("" + i));
						++i;
					}
				} else if (fieldValue instanceof Map) {
					// Validate keys
					for (Object key : ((Map<?, ?>) fieldValue).keySet()) {
						if (!key.getClass().isPrimitive()) {
							this.doValidate(key, context,
									node.child(name).child("key"));
						}
					}
					// Validate values
					for (Object key : ((Map<?, ?>) fieldValue).keySet()) {
						Object value = ((Map<?, ?>) fieldValue).get(key);
						this.doValidate(value, context,
								node.child(name).child(key.toString()));
					}
				} else if (fieldValue.getClass().isArray()) {
					Object[] array = (Object[]) fieldValue;
					int i = 0;
					for (Object item : array) {
						this.doValidate(item, context,
								node.child(name).child("" + i));
						++i;
					}
				} else {
					this.doValidate(fieldValue, context, node.child(name));
				}
			}
		}
	}

	/**
	 * Validate value by annotation.
	 *
	 * @param annotation
	 * @param value
	 * @return
	 */
	@SneakyThrows
	public <T> Optional<ConstraintViolation> validateByAnnotation(
			Annotation annotation,
			String name,
			T value) {
		final Constraint constraint = annotation.annotationType()
				.getAnnotation(Constraint.class);
		if (constraint == null) {
			logger.debug("{} doesn't have a @Constraint annotation.",
					annotation);
			return Optional.empty();
		}
		final Class<? extends ConstraintValidator> constraintValidatorClass = constraint
				.validatedBy();
		final ConstraintValidator constraintValidator = constraintValidatorClass
				.newInstance();
		if (constraintValidator.isValid(annotation, value)) {
			return Optional.empty();
		} else {
			return Optional
					.of(new ConstraintViolation(value, annotation, name));
		}
	}
}
