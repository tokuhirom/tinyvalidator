package me.geso.tinyvalidator;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import me.geso.tinyvalidator.constraints.NotNull;
import me.geso.tinyvalidator.constraints.Pattern;
import me.geso.tinyvalidator.constraints.Size;
import me.geso.tinyvalidator.rules.PatternRule;
import me.geso.tinyvalidator.rules.SizeRule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Validator {
	private static Logger logger = LoggerFactory.getLogger(Validator.class);
	private Map<Class<? extends Annotation>, Rule> rules;
	private static Map<Class<?>, PropertyAccessor[]> accessorCache = new ConcurrentHashMap<>();

	public Validator() {
		this.rules = new HashMap<>();
		this.registerDefaultRules();
	}

	private void registerDefaultRules() {
		this.rules.put(Pattern.class, new PatternRule());
		this.rules.put(Size.class, new SizeRule());
	}

	/**
	 * Add new rule for validator.
	 * 
	 * @param annotation
	 * @param rule
	 */
	public void addRule(Class<? extends Annotation> annotation, Rule rule) {
		this.rules.put(annotation, rule);
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
		if (target.getClass().getPackage().getName().startsWith("java.")) {
			if (logger.isDebugEnabled()) {
				logger.debug(
						"Target class is a built-in object '{}'... Just be ignored.",
						target.getClass());
			}
			return;
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
					accessorList.add(new PropertyAccessor(bean, descriptor));
				}
			} catch (IntrospectionException e) {
				throw new RuntimeException(e);
			}
			accessors = accessorList.stream().toArray(PropertyAccessor[]::new);
			accessorCache.put(bean.getClass(), accessors);
		}
		return accessors;
	}

	private <T> void validateField(T root, Object target,
			List<ConstraintViolation<T>> violations, List<String> route,
			Set<Object> seen, PropertyAccessor accessor) {
		String name = accessor.getName();
		Object fieldValue = accessor.get(target);

		Optional<NotNull> notNullAnnotation = accessor.getNotNullAnnotation();
		if (logger.isDebugEnabled()) {
			logger.debug("{}.{}'s notNullAnnotation: {}", route, accessor.getName(), notNullAnnotation);
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
			Rule rule = rules.get(annotation.annotationType());
			if (rule != null) {
				if (!rule.validate(root, target, route, name, annotation,
						fieldValue)) {
					List<String> currentRoute = new ArrayList<>(route);
					currentRoute.add(name);
					violations.add(new ConstraintViolation<T>(root, target,
							annotation,
							currentRoute));
				}
			}
		}

		// Checking child by recursion.
		if (fieldValue != null
				&& Object.class.isAssignableFrom(fieldValue.getClass())) {
			if (!seen.contains(fieldValue)) {
				List<String> currentRoute = new ArrayList<>(route);
				currentRoute.add(name);
				doValidate(root, fieldValue, violations, route, seen);
			}
		}
	}

}
