package me.geso.tinyvalidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import lombok.Data;
import me.geso.tinyvalidator.constraints.NotNull;

public class NestedTest {

	@Test
	public void testSuccess() {
		Foo foo = new Foo();
		Bar bar = new Bar();
		bar.setX("hoge");
		foo.setBar(bar);
		Validator validator = new Validator();
		List<Violation<Foo>> violations = validator.validate(foo);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testFail1() {
		Foo foo = new Foo();
		Bar bar = new Bar();
		bar.setX(null); // <-- err
		foo.setBar(bar);
		Validator validator = new Validator();
		List<Violation<Foo>> violations = validator.validate(foo);
		assertFalse(violations.isEmpty());
		assertEquals(1, violations.size());
	}

	@Data
	public static class Foo {
		@NotNull
		private Bar bar;
	}

	@Data
	public static class Bar {
		@NotNull
		private String x;
	}
}
