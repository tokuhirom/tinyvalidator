package me.geso.tinyvalidator.rules;

import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import me.geso.tinyvalidator.Validator;
import me.geso.tinyvalidator.ConstraintViolation;
import me.geso.tinyvalidator.constraints.Pattern;

import org.junit.Test;

public class PatternRuleTest {

	@Test
	public void testSuccess() {
		Foo foo = new Foo();
		foo.setBar("5963");
		Validator validator = new Validator();
		List<ConstraintViolation> violations = validator.validate(foo);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testFail() {
		Foo foo = new Foo();
		foo.setBar("hoge");
		Validator validator = new Validator();
		List<ConstraintViolation> violations = validator.validate(foo);
		assertFalse(violations.isEmpty());
		String msg = violations.stream()
				.map(violation -> violation.getName() + " " + violation.getMessage())
				.collect(Collectors.joining(":::"));
		assertEquals("bar must match \\A[0-9]+\\z", msg);
	}

	@Data
	public static class Foo {
		@Pattern(regexp = "\\A[0-9]+\\z")
		private String bar;
	}
}
