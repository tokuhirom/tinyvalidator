package me.geso.tinyvalidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import lombok.Data;
import me.geso.tinyvalidator.constraints.NotNull;

public class ArraysTest {

	@Test
	public void testSuccess() {
		Bar bar = new Bar();
		bar.setBaz("hoge");
		Foo foo = new Foo();
		foo.setBar(new Bar[]{bar});
		Validator validator = new Validator();
		List<ConstraintViolation> violations = validator.validate(foo);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testFail() {
		Bar bar = new Bar();
		Foo foo = new Foo();
		foo.setBar(new Bar[]{bar});

		Validator validator = new Validator();
		List<ConstraintViolation> violations = validator.validate(foo);
		assertFalse(violations.isEmpty());
		String msg = violations
			.stream()
			.map(violation -> violation.getName() + " "
				+ violation.getMessage())
			.collect(Collectors.joining(":::"));
		assertEquals("bar.0.baz may not be null.", msg);
	}

	@Data
	public static class Foo {
		@Valid
		@NotNull
		private Bar[] bar;
	}

	@Data
	public static class Bar {
		@NotNull
		private String baz;
	}

}
