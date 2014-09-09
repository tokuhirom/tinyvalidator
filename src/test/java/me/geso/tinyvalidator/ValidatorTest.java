package me.geso.tinyvalidator;

import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Collectors;

import me.geso.tinyvalidator.constraints.NotNull;

import org.junit.Test;

public class ValidatorTest {

	@Test
	public void testSuccess() {
		Foo foo = new Foo();
		foo.setBar("hoge");
		foo.setBaz(5);
		Validator validator = new Validator();
		List<ConstraintViolation<Foo>> violations = validator.validate(foo);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testFail() {
		Foo foo = new Foo();
		foo.setBar(null);
		foo.setBaz(5);
		Validator validator = new Validator();
		List<ConstraintViolation<Foo>> violations = validator.validate(foo);
		assertFalse(violations.isEmpty());
		assertEquals(1, violations.size());
		String msg = violations.stream()
				.map(violation -> {
					return violation.getRoutePath() + " " + violation.getMessage();
				})
				.collect(Collectors.joining(":::"));
		assertEquals("bar may not be null.", msg);
	}

	@Test
	public void testFail2Params() {
		Foo foo = new Foo();
		Validator validator = new Validator();
		List<ConstraintViolation<Foo>> violations = validator.validate(foo);
		assertFalse(violations.isEmpty());
		String msg = violations.stream()
				.map(violation -> {
					return violation.getRoutePath() + " " + violation.getMessage();
				})
				.collect(Collectors.joining(":::"));
		assertEquals("bar may not be null.:::baz may not be null.", msg);
	}

	public static class Foo {
		private String bar;

		private Integer baz;

		@NotNull
		public String getBar() {
			return bar;
		}

		public void setBar(String bar) {
			this.bar = bar;
		}

		@NotNull
		public Integer getBaz() {
			return baz;
		}

		public void setBaz(Integer baz) {
			this.baz = baz;
		}
	}

}
