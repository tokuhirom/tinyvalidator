package me.geso.tinyvalidator.constraints;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import org.junit.Test;

public class EmailTest {
	private final Email.Validator validator = new Email.Validator();

	@Test
	public void test() {
		// int
		assertFalse(validator.isValid(null, 3));
		// null
		assertTrue(validator.isValid(null, null));
		// bad address
		assertFalse(validator.isValid(null, "hoge ^^;"));
		// good address
		assertTrue(validator.isValid(null, "example@gmail.com"));
	}

}
