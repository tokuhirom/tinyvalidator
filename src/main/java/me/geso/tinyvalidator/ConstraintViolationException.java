package me.geso.tinyvalidator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * You can throw this exception if you found a constraint violation.<br>
 * tinyvalidator never throw this exception. *You* can throw this.
 */
public class ConstraintViolationException extends Exception {
	private static final long serialVersionUID = 1L;

	private final List<ConstraintViolation> violations;

	public ConstraintViolationException(final List<ConstraintViolation> violations) {
		super(violations.stream()
			.map(it -> it.getName() + " " + it.getMessage())
			.collect(Collectors.joining("\n")));

		this.violations = violations;
	}

	public List<ConstraintViolation> getViolations() {
		return violations;
	}
}
