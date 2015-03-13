package me.geso.tinyvalidator.constraints;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class ValidatorTest {
	private final HttpUrl.Validator validator = new HttpUrl.Validator();

	@Test
	public void test() {
		assertTrue(validator.isValid(null, null));
	}

	@Test
	public void testValid() {
		assertTrue(validator.isValid(null, "http://mixi.jp/"));
	}

	@Test
	public void testInvalid2() {
		assertFalse(validator.isValid(null, "xhttp://mixi.jp/"));
	}

	@Test
	public void testInvalid() {
		assertFalse(validator.isValid(null, "javascript:alert(0)"));
	}

}
