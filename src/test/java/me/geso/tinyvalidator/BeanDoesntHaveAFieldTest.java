package me.geso.tinyvalidator;

import static org.junit.Assert.assertEquals;

import java.util.stream.Collectors;

import org.junit.Test;

import me.geso.tinyvalidator.constraints.NotNull;

public class BeanDoesntHaveAFieldTest {
	@Test
	public void testFoo() {
		String msg = new Validator().validate(new Foo()).stream()
			.map(it -> it.getName() + " " + it.getMessage())
			.collect(Collectors.joining(","));
		assertEquals("bar may not be null.", msg);
	}

	public static class Foo {
		@NotNull
		public String getBar() {
			return null;
		}
	}
}
