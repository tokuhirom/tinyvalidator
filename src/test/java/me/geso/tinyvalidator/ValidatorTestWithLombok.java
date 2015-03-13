package me.geso.tinyvalidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;
import me.geso.tinyvalidator.constraints.NotNull;

public class ValidatorTestWithLombok {
	private static Logger logger = LoggerFactory
		.getLogger(ValidatorTestWithLombok.class);

	@Test
	public void testSuccess() {
		Foo foo = new Foo();
		foo.setBar("hoge");
		foo.setBaz(5);
		Validator validator = new Validator();
		List<ConstraintViolation> violations = validator.validate(foo);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testFail() {
		Foo foo = new Foo();
		foo.setBar(null);
		foo.setBaz(5);
		Validator validator = new Validator();
		List<ConstraintViolation> violations = validator.validate(foo);
		assertFalse(violations.isEmpty());
		String msg = violations.stream()
			.map(violation -> violation.getName() + " " + violation.getMessage())
			.collect(Collectors.joining(":::"));
		assertEquals("bar may not be null.", msg);
	}

	@Test
	public void testFail2Params() {
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "default");
		logger.info("HOGE");

		Foo foo = new Foo();
		Validator validator = new Validator();
		List<ConstraintViolation> violations = validator.validate(foo);
		assertFalse(violations.isEmpty());
		String msg = violations.stream()
			.map(violation -> violation.getName() + " " + violation.getMessage())
			.collect(Collectors.joining(":::"));
		assertEquals("bar may not be null.:::baz may not be null.", msg);
	}

	@Data
	public static class Foo {
		@NotNull
		private String bar;
		@NotNull
		private Integer baz;
	}

}
