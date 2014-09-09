package me.geso.tinyvalidator.rules;

import lombok.Data;
import lombok.ToString;
import me.geso.tinyvalidator.ConstraintViolation;
import me.geso.tinyvalidator.Validator;
import me.geso.tinyvalidator.constraints.Size;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class SizeConstraintValidatorTest {
    private static Logger logger = LoggerFactory.getLogger(SizeConstraintValidatorTest.class);

    @Data
    @ToString
    public static class StringFoo {
        @Size(min = 2, max = 5)
        private String bar;

        @Test
        public void testTrueString() {
            StringFoo stringFoo = new StringFoo();
            stringFoo.setBar("hoge");
            List<ConstraintViolation<StringFoo>> violations = new Validator().validate(stringFoo);
            assertTrue(violations.isEmpty());
        }

        @Test
        public void testFalseString() {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "default");
            logger.info("HHA");

            StringFoo stringFoo = new StringFoo();
            stringFoo.setBar("hogeeeee");
            List<ConstraintViolation<StringFoo>> violations = new Validator().validate(stringFoo);
            String msg = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .collect(Collectors.joining(":::"));
            assertEquals("bar size must be between 2 and 5", msg);
            assertTrue(!violations.isEmpty());
        }

        @Test
        public void testFalseStringTooShort() {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "default");

            StringFoo stringFoo = new StringFoo();
            stringFoo.setBar("h");
            List<ConstraintViolation<StringFoo>> violations = new Validator().validate(stringFoo);
            String msg = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .collect(Collectors.joining(":::"));
            assertEquals("bar size must be between 2 and 5", msg);
            assertTrue(!violations.isEmpty());
        }
    }

    @Data
    @ToString
    public static class CollectionFoo {
        @Size(min = 2, max = 5)
        private List<String> bar;

        @Test
        public void testSuccessCollection() {
            CollectionFoo foo = new CollectionFoo();
            foo.setBar(Arrays.asList("hoge", "fuga"));
            List<ConstraintViolation<CollectionFoo>> violations = new Validator().validate(foo);
            assertTrue(violations.isEmpty());
        }

        @Test
        public void testFailCollectionTooShort() {
            CollectionFoo foo = new CollectionFoo();
            foo.setBar(Arrays.asList());
            List<ConstraintViolation<CollectionFoo>> violations = new Validator().validate(foo);
            assertFalse(violations.isEmpty());
            String msg = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .collect(Collectors.joining(":::"));
            assertEquals("bar size must be between 2 and 5", msg);
        }

        @Test
        public void testFailCollectionTooLarge() {
            CollectionFoo foo = new CollectionFoo();
            foo.setBar(Arrays.asList("a", "b", "c", "d", "e", "f"));
            List<ConstraintViolation<CollectionFoo>> violations = new Validator().validate(foo);
            assertFalse(violations.isEmpty());
            String msg = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .collect(Collectors.joining(":::"));
            assertEquals("bar size must be between 2 and 5", msg);
        }
    }

    @Data
    @ToString
    public static class MapFoo {
        @Size(min = 1, max = 2)
        private Map<String, String> bar;

        @Test
        public void testSuccessMap() {
            MapFoo foo = new MapFoo();
            Map<String, String> map = new HashMap<>();
            map.put("a", "1");
            foo.setBar(map);
            List<ConstraintViolation<MapFoo>> violations = new Validator().validate(foo);
            assertTrue(violations.isEmpty());
        }

        @Test
        public void testFailMapTooSmall() {
            MapFoo foo = new MapFoo();
            Map<String, String> map = new HashMap<>();
            foo.setBar(map);
            List<ConstraintViolation<MapFoo>> violations = new Validator().validate(foo);
            assertFalse(violations.isEmpty());
            String msg = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
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
            List<ConstraintViolation<MapFoo>> violations = new Validator().validate(foo);
            assertFalse(violations.isEmpty());
            String msg = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .collect(Collectors.joining(":::"));
            assertEquals("bar size must be between 1 and 2", msg);
        }
    }

    @Data
    @ToString
    public static class ArrayFoo {
        @Size(min = 2, max = 3)
        private int[] bar;

        @Test
        public void testSuccessMap() {
            ArrayFoo foo = new ArrayFoo();
            foo.setBar(new int[] {1,2});
            List<ConstraintViolation<ArrayFoo>> violations = new Validator().validate(foo);
            assertTrue(violations.isEmpty());
        }

        @Test
        public void testFailMapTooSmall() {
            ArrayFoo foo = new ArrayFoo();
            foo.setBar(new int[] {1});
            List<ConstraintViolation<ArrayFoo>> violations = new Validator().validate(foo);
            assertFalse(violations.isEmpty());
            String msg = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .collect(Collectors.joining(":::"));
            assertEquals("bar size must be between 2 and 3", msg);
        }

        @Test
        public void testFailMapTooLarge() {
            ArrayFoo foo = new ArrayFoo();
            foo.setBar(new int[] {1,2,3,4,5});
            List<ConstraintViolation<ArrayFoo>> violations = new Validator().validate(foo);
            assertFalse(violations.isEmpty());
            String msg = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .collect(Collectors.joining(":::"));
            assertEquals("bar size must be between 2 and 3", msg);
        }
    }

}
