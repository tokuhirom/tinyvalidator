package me.geso.tinyvalidator.rules;

import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import me.geso.tinyvalidator.DefaultMessageGenerator;
import me.geso.tinyvalidator.Validator;
import me.geso.tinyvalidator.Violation;
import me.geso.tinyvalidator.constraints.Size;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SizeRuleTest {
	private static Logger logger = LoggerFactory.getLogger(SizeRuleTest.class);

	@Test
	public void testTrueString() {
		StringFoo stringFoo = new StringFoo();
		stringFoo.setBar("hoge");
		List<Violation<StringFoo>> violations = new Validator().validate(stringFoo);
		DefaultMessageGenerator gen = new DefaultMessageGenerator();
		String msg = violations.stream().map(it -> gen.generateMessage(it)).collect(Collectors.joining(","));
		assertEquals("", msg);
	}

	@Test
	public void testFalseString() {
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "default");
		logger.info("HHA");

		StringFoo stringFoo = new StringFoo();
		stringFoo.setBar("hogeeeee");
		List<Violation<StringFoo>> violations = new Validator().validate(stringFoo);
		DefaultMessageGenerator gen = new DefaultMessageGenerator();
		String msg = violations.stream().map(it -> gen.generateMessage(it)).collect(Collectors.joining(","));
		assertEquals("bar: 2 < SizeRuleTest.StringFoo(bar=hogeeeee) < 5", msg);
		assertTrue(!violations.isEmpty());
	}

	@Test
	public void testFalseStringTooShort() {
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "default");

		StringFoo stringFoo = new StringFoo();
		stringFoo.setBar("h");
		List<Violation<StringFoo>> violations = new Validator().validate(stringFoo);
		DefaultMessageGenerator gen = new DefaultMessageGenerator();
		String msg = violations.stream().map(it -> gen.generateMessage(it)).collect(Collectors.joining(","));
		assertEquals("bar: 2 < SizeRuleTest.StringFoo(bar=h) < 5", msg);
		assertTrue(!violations.isEmpty());
	}
	
	@Data
	public static class StringFoo {
		@Size(min=2, max=5)
		private String bar;
	}

}
