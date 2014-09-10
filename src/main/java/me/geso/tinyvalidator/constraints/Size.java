package me.geso.tinyvalidator.constraints;

import me.geso.tinyvalidator.Constraint;
import me.geso.tinyvalidator.rules.SizeConstraintValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Constraint(validatedBy = SizeConstraintValidator.class)
public @interface Size {
    /**
     * @return size the element must be higher or equal to
     */
    int min() default 0;

    /**
     * @return size the element must be lower or equal to
     */
    int max() default Integer.MAX_VALUE;

    String message() default "size must be between {min} and {max}";
}
