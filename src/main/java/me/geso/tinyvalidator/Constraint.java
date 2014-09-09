package me.geso.tinyvalidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by tokuhirom on 9/10/14.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Constraint {
    public Class<? extends ConstraintValidator>validatedBy();
}
