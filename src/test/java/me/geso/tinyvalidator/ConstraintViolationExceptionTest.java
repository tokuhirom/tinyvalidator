package me.geso.tinyvalidator;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import lombok.Value;
import me.geso.tinyvalidator.constraints.NotNull;

public class ConstraintViolationExceptionTest {

	@Test
	public void test() {
		Foo foo = new Foo(null);
		List<ConstraintViolation> violations = new Validator().validate(foo);
		ConstraintViolationException constraintViolationException = new ConstraintViolationException(violations);
		assertEquals("x may not be null.", constraintViolationException.getMessage());
	}

	@Value
	public static class Foo {
		@NotNull
		String x;
	}

}
