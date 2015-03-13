package me.geso.tinyvalidator.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import me.geso.tinyvalidator.ConstraintViolation;
import me.geso.tinyvalidator.Validator;
import me.geso.tinyvalidator.constraints.Size;

@Slf4j
public class SizeConstraintValidatorTest {
	@Test
	public void testTrueString() {
		StringFoo stringFoo = new StringFoo();
		stringFoo.setBar("hoge");
		List<ConstraintViolation> violations = new Validator().validate(stringFoo);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testTrueStringSurrogate() {
		StringFoo stringFoo = new StringFoo();
		stringFoo.setBar("𠮷𠮷𠮷𠮷");
		List<ConstraintViolation> violations = new Validator().validate(stringFoo);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testFalseString() {
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "default");
		log.info("HHA");

		StringFoo stringFoo = new StringFoo();
		stringFoo.setBar("hogeeeee");
		List<ConstraintViolation> violations = new Validator().validate(stringFoo);
		String msg = violations.stream()
			.map(violation -> violation.getName() + " " + violation.getMessage())
			.collect(Collectors.joining(":::"));
		assertEquals("bar size must be between 2 and 5", msg);
		assertTrue(!violations.isEmpty());
	}

	@Test
	public void testFalseStringTooShort() {
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "default");

		StringFoo stringFoo = new StringFoo();
		stringFoo.setBar("h");
		List<ConstraintViolation> violations = new Validator().validate(stringFoo);
		String msg = violations.stream()
			.map(violation -> violation.getName() + " " + violation.getMessage())
			.collect(Collectors.joining(":::"));
		assertEquals("bar size must be between 2 and 5", msg);
		assertTrue(!violations.isEmpty());
	}

	@Data
	@ToString
	public static class StringFoo {
		@Size(min = 2, max = 5)
		private String bar;

	}

	@Test
	public void testSuccessCollection() {
		CollectionFoo foo = new CollectionFoo();
		foo.setBar(Arrays.asList("hoge", "fuga"));
		List<ConstraintViolation> violations = new Validator().validate(foo);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testFailCollectionTooShort() {
		CollectionFoo foo = new CollectionFoo();
		foo.setBar(Arrays.asList());
		List<ConstraintViolation> violations = new Validator().validate(foo);
		assertFalse(violations.isEmpty());
		String msg = violations.stream()
			.map(violation -> violation.getName() + " " + violation.getMessage())
			.collect(Collectors.joining(":::"));
		assertEquals("bar size must be between 2 and 5", msg);
	}

	@Test
	public void testFailCollectionTooLarge() {
		CollectionFoo foo = new CollectionFoo();
		foo.setBar(Arrays.asList("a", "b", "c", "d", "e", "f"));
		List<ConstraintViolation> violations = new Validator().validate(foo);
		assertFalse(violations.isEmpty());
		String msg = violations.stream()
			.map(violation -> violation.getName() + " " + violation.getMessage())
			.collect(Collectors.joining(":::"));
		assertEquals("bar size must be between 2 and 5", msg);
	}

	@Data
	@ToString
	public static class CollectionFoo {
		@Size(min = 2, max = 5)
		private List<String> bar;

	}

	@Test
	public void testSuccessMap() {
		MapFoo foo = new MapFoo();
		Map<String, String> map = new HashMap<>();
		map.put("a", "1");
		foo.setBar(map);
		List<ConstraintViolation> violations = new Validator().validate(foo);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testFailMapTooSmall() {
		MapFoo foo = new MapFoo();
		Map<String, String> map = new HashMap<>();
		foo.setBar(map);
		List<ConstraintViolation> violations = new Validator().validate(foo);
		assertFalse(violations.isEmpty());
		String msg = violations.stream()
			.map(violation -> violation.getName() + " " + violation.getMessage())
			.collect(Collectors.joining(":::"));
		assertEquals("bar size must be between 1 and 2", msg);
	}

	@Test
	public void testFailMapTooLarge() {
		MapFoo foo = new MapFoo();
		Map<String, String> map = new HashMap<>();
		map.put("a", "1");
		map.put("b", "2");
		map.put("c", "3");
		foo.setBar(map);
		List<ConstraintViolation> violations = new Validator().validate(foo);
		assertFalse(violations.isEmpty());
		String msg = violations.stream()
			.map(violation -> violation.getName() + " " + violation.getMessage())
			.collect(Collectors.joining(":::"));
		assertEquals("bar size must be between 1 and 2", msg);
	}

	@Data
	@ToString
	public static class MapFoo {
		@Size(min = 1, max = 2)
		private Map<String, String> bar;

	}

	@Test
	public void testSuccessArray() {
		ArrayFoo foo = new ArrayFoo();
		foo.setBar(new int[]{1, 2});
		List<ConstraintViolation> violations = new Validator().validate(foo);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testFailArrayTooSmall() {
		ArrayFoo foo = new ArrayFoo();
		foo.setBar(new int[]{1});
		List<ConstraintViolation> violations = new Validator().validate(foo);
		assertFalse(violations.isEmpty());
		String msg = violations.stream()
			.map(violation -> violation.getName() + " " + violation.getMessage())
			.collect(Collectors.joining(":::"));
		assertEquals("bar size must be between 2 and 3", msg);
	}

	@Test
	public void testFailArrayTooLarge() {
		ArrayFoo foo = new ArrayFoo();
		foo.setBar(new int[]{1, 2, 3, 4, 5});
		List<ConstraintViolation> violations = new Validator().validate(foo);
		assertFalse(violations.isEmpty());
		String msg = violations.stream()
			.map(violation -> violation.getName() + " " + violation.getMessage())
			.collect(Collectors.joining(":::"));
		assertEquals("bar size must be between 2 and 3", msg);
	}

	@Data
	@ToString
	public static class ArrayFoo {
		@Size(min = 2, max = 3)
		private int[] bar;

	}

}
