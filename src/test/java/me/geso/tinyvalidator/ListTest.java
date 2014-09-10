package me.geso.tinyvalidator;


import lombok.Data;
import me.geso.tinyvalidator.constraints.NotNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ListTest {
    private static Logger logger = LoggerFactory
            .getLogger(ListTest.class);

    @Test
    public void testSuccess() {
    	Bar bar = new Bar();
    	bar.setBaz("hoge");
        Foo foo = new Foo();
        foo.setBar(Arrays.asList(bar));
        Validator validator = new Validator();
        List<ConstraintViolation> violations = validator.validate(foo);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testFail() {
    	Bar bar = new Bar();
        Foo foo = new Foo();
        foo.setBar(Arrays.asList(bar));

        Validator validator = new Validator();
        List<ConstraintViolation> violations = validator.validate(foo);
        assertFalse(violations.isEmpty());
        String msg = violations.stream()
                .map(violation -> violation.getName() + " " + violation.getMessage())
                .collect(Collectors.joining(":::"));
        assertEquals("bar.0.baz may not be null.", msg);
    }

    @Data
    public static class Foo {
    	@Valid
        @NotNull
        private List<Bar> bar;
    }

    @Data
    public static class Bar {
        @NotNull
        private String baz;
    }

}
