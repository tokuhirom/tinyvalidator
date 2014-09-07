package me.geso.tinyvalidator;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.geso.tinyvalidator.constraints.NotNull;
import me.geso.tinyvalidator.constraints.Pattern;
import me.geso.tinyvalidator.constraints.Size;
import me.geso.tinyvalidator.rules.PatternRule;
import me.geso.tinyvalidator.rules.SizeRule;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Validator {
	private static Logger logger = LoggerFactory.getLogger(Validator.class);
	private Map<Class<? extends Annotation>, Rule> rules;

	public Validator() {
		this.rules = new HashMap<>();
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

		try {
			seen.add(target);

			// Validate by properties.
			{
				PropertyDescriptor[] propertyDescriptors = BeanUtilsBean
						.getInstance().getPropertyUtils()
						.getPropertyDescriptors(target);
				for (PropertyDescriptor descriptor : propertyDescriptors) {
					if ("classLoader".equals(descriptor.getName())
							|| "class".equals(descriptor.getName())) {
						continue;
					}

					if (logger.isDebugEnabled()) {
						logger.debug(
								"Checking root: {}, target: {} descriptor: {}",
								root.getClass(), target.getClass(),
								descriptor.getName());
					}
					Method getter = MethodUtils.getAccessibleMethod(
							target.getClass(), descriptor.getReadMethod());
					Object fieldValue = null;
					if (getter != null) {
						fieldValue = getter.invoke(target);
					}

					Set<Annotation> annotations = new HashSet<>();
					for (Annotation annotation : getter.getAnnotations()) {
						annotations.add(annotation);
					}

					this.validateField(root, target, violations,
							route, seen, descriptor.getName(), annotations,
							fieldValue);
				}
			}

			// Validate by fields.
			if (logger.isDebugEnabled()) {
				logger.debug(
						"fields: {}, {}", target.getClass(), target.getClass()
								.getFields());
			}
			for (Field field : target.getClass().getDeclaredFields()) {
				if (logger.isDebugEnabled()) {
					logger.debug(
							"Checking by field:: root: {}, target: {} descriptor: {}",
							root.getClass(), target.getClass(),
							field.getName());
				}

				field.setAccessible(true);
				Object fieldValue = field.get(target);
				Set<Annotation> annotations = new HashSet<>();
				for (Annotation annotation : field.getAnnotations()) {
					annotations.add(annotation);
				}
				this.validateField(root, target, violations, route, seen,
						field.getName(), annotations, fieldValue);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private <T> void validateField(T root, Object target,
			List<Violation<T>> violations, List<String> route,
			Set<Object> seen, String name, Set<Annotation> annotations,
			Object fieldValue) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof NotNull) {
				List<String> currentRoute = new ArrayList<>(route);
				currentRoute.add(name);
				violations.add(new Violation<T>(root, target,
						annotation,
						currentRoute));
			}
		}

		for (Annotation annotation : annotations) {
			Rule rule = rules.get(annotation.annotationType());
			if (rule != null) {
				if (!rule.validate(root, target, route, name, annotation, fieldValue)) {
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
