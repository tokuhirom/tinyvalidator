package me.geso.tinyvalidator;

import static org.junit.Assert.assertEquals;

import java.util.stream.Collectors;

import me.geso.tinyvalidator.constraints.NotNull;

import org.junit.Test;

public class BeanDoesntHaveAFieldTest {
	@Test
	public void testFoo() {
		MessageGenerator mg = new DefaultMessageGenerator();
		String msg = new Validator().validate(new Foo()).stream()
				.map(it -> mg.generateMessage(it))
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
