package me.geso.tinyvalidator;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
	private static Map<Class<?>, Accessor[]> accessorCache = new ConcurrentHashMap<>();

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
	public <T> List<Violation<T>> validate(T bean) {
		List<Violation<T>> violations = new ArrayList<>();
		Set<Object> seen = new HashSet<>();
		doValidate(bean, bean, violations, new ArrayList<String>(), seen);
		return violations;
	}

	private <T> void doValidate(T root, Object target,
			List<Violation<T>> violations,
			List<String> route,
			Set<Object> seen) {
		if (logger.isDebugEnabled()) {
			logger.debug(
					"Checking {}", target.getClass());
		}
		if (target instanceof Class) {
			if (logger.isDebugEnabled()) {
				logger.debug(
						"Target class is a Class object... Just be ignored.");
			}
			return;
		} else if (target instanceof ClassLoader) {
			if (logger.isDebugEnabled()) {
				logger.debug(
						"Target class is a ClassLoader object... Just be ignored.");
			}
			return;
		} else if (target instanceof Number) {
			if (logger.isDebugEnabled()) {
				logger.debug(
						"Target class is a Number object... Just be ignored.");
			}
			return;
		} else if (target instanceof Boolean) {
			if (logger.isDebugEnabled()) {
				logger.debug(
						"Target class is a Boolean object... Just be ignored.");
			}
			return;
		} else if (target instanceof String) {
			if (logger.isDebugEnabled()) {
				logger.debug(
						"Target class is a String object... Just be ignored.");
			}
			return;
		}

		seen.add(target);

		Accessor[] accessors = this.getAccessors(target);
		for (Accessor accessor : accessors) {
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

	private Accessor[] getAccessors(Object bean) {
		Accessor[] accessors = accessorCache.get(bean.getClass());
		if (accessors == null) {
			List<Accessor> accessorList = new ArrayList<>();
			try {
				BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
				for (PropertyDescriptor descriptor : beanInfo
						.getPropertyDescriptors()) {
					if ("class".equals(descriptor.getName())
							|| "classLoader".equals(descriptor.getName())) {
						continue;
					}
					if (descriptor.getReadMethod().getAnnotations().length > 0) {
						accessorList.add(new PropertyAccessor(descriptor));
					}
				}
			} catch (IntrospectionException e) {
				throw new RuntimeException(e);
			}
			for (Field field : bean.getClass().getDeclaredFields()) {
				if (field.getAnnotations().length > 0) {
					accessorList.add(new FieldAccessor(field));
				}
			}
			accessors = accessorList.toArray(new Accessor[0]);
			accessorCache.put(bean.getClass(), accessors);
		}
		return accessors;
	}

	private <T> void validateField(T root, Object target,
			List<Violation<T>> violations, List<String> route,
			Set<Object> seen, Accessor accessor) {
		String name = accessor.getName();
		Object fieldValue = accessor.get(target);

		NotNull notNullAnnotation = accessor.getAnnotation(NotNull.class);
		if (notNullAnnotation != null) {
			if (fieldValue == null) {
				List<String> currentRoute = new ArrayList<>(route);
				currentRoute.add(name);
				violations.add(new Violation<T>(root, target,
						notNullAnnotation,
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
					violations.add(new Violation<T>(root, target,
							annotation,
							currentRoute));
				}
			}
		}

		// Checking child by recursion.
		if (fieldValue != null
				&& Object.class.isAssignableFrom(fieldValue.getClass())) {
			if (!seen.contains(target)) {
				List<String> currentRoute = new ArrayList<>(route);
				currentRoute.add(name);
				doValidate(root, fieldValue, violations, route, seen);
			}
		}
	}

}
