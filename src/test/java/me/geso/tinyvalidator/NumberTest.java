package me.geso.tinyvalidator;

import lombok.Data;
import me.geso.tinyvalidator.constraints.Max;
import me.geso.tinyvalidator.constraints.Min;
import me.geso.tinyvalidator.constraints.NotNull;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class NumberTest {

	@Test
	public void testSuccess() {
		Bar bar = new Bar();
		bar.setAfield(2);
		Foo foo = new Foo();
		foo.setAfield(12);
		Validator validator = new Validator();
		List<ConstraintViolation> violations = validator.validate(foo);
		assertTrue(violations.isEmpty());

		violations = validator.validate(bar);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testFail() {
		Bar bar = new Bar();
		Foo foo = new Foo();
		foo.setAfield(2);
		bar.setAfield(12);

		Validator validator = new Validator();
		List<ConstraintViolation> violations = validator.validate(foo);
		assertFalse(violations.isEmpty());
		String msg = violations
			.stream()
			.map(violation -> violation.getName() + " "
				+ violation.getMessage())
			.collect(Collectors.joining(":::"));
		assertEquals("afield value must be >= 10.", msg);

		violations = validator.validate(bar);
		assertFalse(violations.isEmpty());
		msg = violations
				.stream()
				.map(violation -> violation.getName() + " "
						+ violation.getMessage())
				.collect(Collectors.joining(":::"));
		assertEquals("afield value must be <= 10.", msg);
	}

	@Data
	public static class Foo {
		@Min(value=10)
		private Integer afield;
	}

	@Data
	public static class Bar {
		@Max(value=10)
		private Integer afield;
	}

}
