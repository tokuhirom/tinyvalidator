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
		List<Violation<Foo>> violations = validator.validate(foo);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testFail() {
		Foo foo = new Foo();
		foo.setBar(null);
		foo.setBaz(5);
		Validator validator = new Validator();
		List<Violation<Foo>> violations = validator.validate(foo);
		assertFalse(violations.isEmpty());
		MessageGenerator msggen = new DefaultMessageGenerator();
		String msg = violations.stream().map(violation -> msggen.generateMessage(violation)).collect(Collectors.joining(":::"));
		assertEquals("You should fill bar.", msg);
	}

	@Test
	public void testFail2Params() {
		Foo foo = new Foo();
		Validator validator = new Validator();
		List<Violation<Foo>> violations = validator.validate(foo);
		assertFalse(violations.isEmpty());
		MessageGenerator msggen = new DefaultMessageGenerator();
		String msg = violations.stream().map(violation -> msggen.generateMessage(violation)).collect(Collectors.joining(":::"));
		assertEquals("You should fill bar.:::You should fill baz.", msg);
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
