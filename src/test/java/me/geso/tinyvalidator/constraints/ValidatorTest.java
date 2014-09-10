package me.geso.tinyvalidator.constraints;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class ValidatorTest {
    @Test
    public void test() {
        HttpUrl.Validator validator = new HttpUrl.Validator();
        assertTrue(validator.isValid(null, null));
    }

    @Test
    public void testValid() {
        HttpUrl.Validator validator = new HttpUrl.Validator();
        assertTrue(validator.isValid(null, "http://mixi.jp/"));
    }

    @Test
    public void testInvalid2() {
        HttpUrl.Validator validator = new HttpUrl.Validator();
        assertFalse(validator.isValid(null, "xhttp://mixi.jp/"));
    }

    @Test
    public void testInvalid() {
        HttpUrl.Validator validator = new HttpUrl.Validator();
        assertFalse(validator.isValid(null, "javascript:alert(0)"));
    }

}