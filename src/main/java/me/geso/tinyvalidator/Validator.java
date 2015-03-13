package me.geso.tinyvalidator;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.geso.tinyvalidator.constraints.NotNull;

/**
 * The validator class.
 */
@Slf4j
public class Validator {
	private static final Map<Class<?>, PropertyAccessor[]> accessorCache = new ConcurrentHashMap<>();

	/**
	 * Create new validator instance.
	 */
	public Validator() {
	}

	/**
	 * Validate bean.
	 *
	 * @param bean target bean object
	 * @return return violations.
	 */
	public <T> List<ConstraintViolation> validate(T bean) {
		ValidationContext context = new ValidationContext();
		Node node = new Node();
		this.doValidate(bean, context, node);
		return context.getViolations();
	}

	private <T> void doValidate(Object value,
			ValidationContext context,
			Node node) {
		if (log.isDebugEnabled()) {
			log.debug(
				"Checking {}", value.getClass());
		}
		if (value.getClass().isPrimitive()) {
			if (log.isDebugEnabled()) {
				log.debug(
					"{} is a primitive type.", value.getClass());
			}
			return;
		}

		context.registerSeen(value);

		PropertyAccessor[] accessors = this.getAccessors(value);
		for (PropertyAccessor accessor : accessors) {
			if (log.isDebugEnabled()) {
				log.debug(
					"Checking target: {} descriptor: {}",
					value.getClass(),
					accessor.getName());
			}

			this.validateField(value, context, accessor, node);
		}
	}

	private PropertyAccessor[] getAccessors(Object bean) {
		return accessorCache.computeIfAbsent(bean.getClass(), klass -> {
			// cache miss.
			List<PropertyAccessor> accessorList = new ArrayList<>();
			try {
				BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
				final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
				if (log.isDebugEnabled()) {
					log.debug("Property descriptors: {}", Arrays.toString(propertyDescriptors));
				}
				for (PropertyDescriptor descriptor : propertyDescriptors) {
					if (descriptor.getReadMethod() == null) {
						if (log.isDebugEnabled()) {
							log.debug("There is no read method: {}", descriptor.getName());
						}
						continue; // ignore property that doesn't have a getter
						// method.
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
			return accessorList.stream().toArray(PropertyAccessor[]::new);
		});
	}

	@SneakyThrows
	private <T> void validateField(Object target,
			ValidationContext context,
			PropertyAccessor accessor,
			Node node) {
		String name = accessor.getName();
		Object fieldValue = accessor.get(target);

		Optional<NotNull> notNullAnnotation = accessor.getNotNullAnnotation();
		if (log.isDebugEnabled()) {
			log.debug("{}.{}'s notNullAnnotation: {}", node.toString(),
				accessor.getName(), notNullAnnotation);
		}
		if (fieldValue == null) {
			if (notNullAnnotation.isPresent()) {
				log.debug("{} is null", node.toString());
				final ConstraintViolation constraintViolation = new ConstraintViolation(
					fieldValue, notNullAnnotation.get(), node.child(name)
						.toString()
						);
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
		if (accessor.getValidAnnotation().isPresent()) {
			if (!context.isSeen(fieldValue)) {
				if (fieldValue instanceof Collection) {
					int i = 0;
					for (Object item : (Collection<?>)fieldValue) {
						this.doValidate(item, context,
							node.child(name).child("" + i));
						++i;
					}
				} else if (fieldValue instanceof Map) {
					// Validate keys
					for (Object key : ((Map<?, ?>)fieldValue).keySet()) {
						if (!key.getClass().isPrimitive()) {
							this.doValidate(key, context,
								node.child(name).child("key"));
						}
					}
					// Validate values
					for (Object key : ((Map<?, ?>)fieldValue).keySet()) {
						Object value = ((Map<?, ?>)fieldValue).get(key);
						this.doValidate(value, context,
							node.child(name).child(key.toString()));
					}
				} else if (fieldValue.getClass().isArray()) {
					Object[] array = (Object[])fieldValue;
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
	 * @param annotation target annotation
	 * @param value target value
	 * @return Validation result.
	 */
	@SneakyThrows
	public <T> Optional<ConstraintViolation> validateByAnnotation(
			Annotation annotation,
			String name,
			T value) {
		final Constraint constraint = annotation.annotationType()
			.getAnnotation(Constraint.class);
		if (constraint == null) {
			log.debug("{} doesn't have a @Constraint annotation.",
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
