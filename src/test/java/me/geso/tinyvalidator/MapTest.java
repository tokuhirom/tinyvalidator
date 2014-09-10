package me.geso.tinyvalidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Data;
import me.geso.tinyvalidator.constraints.NotNull;

import org.junit.Test;

public class MapTest {

	@Test
	public void testSuccess() {
		Bar bar = new Bar();
		Foo foo = new Foo();
		bar.setBaz("hoge");
		Map<String, Bar> map = new HashMap<String, Bar>();
		map.put("hoge", bar);
		foo.setBar(map);
		Validator validator = new Validator();
		List<ConstraintViolation> violations = validator.validate(foo);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testFail() {
		Bar bar = new Bar();
		Foo foo = new Foo();
		Map<String, Bar> map = new HashMap<String, Bar>();
		map.put("hoge", bar);
		foo.setBar(map);

		Validator validator = new Validator();
		List<ConstraintViolation> violations = validator.validate(foo);
		assertFalse(violations.isEmpty());
		String msg = violations
				.stream()
				.map(violation -> violation.getName() + " "
						+ violation.getMessage())
				.collect(Collectors.joining(":::"));
		assertEquals("bar.hoge.baz may not be null.", msg);
	}

	@Data
	public static class Foo {
		@Valid
		@NotNull
		private Map<String, Bar> bar;
	}

	@Data
	public static class Bar {
		@NotNull
		private String baz;
	}

}