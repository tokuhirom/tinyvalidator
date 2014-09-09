package me.geso.tinyvalidator;

/**
 * Created by tokuhirom on 9/10/14.
 */
public @interface Constraint {
    public Class<? extends ConstraintValidator>validatedBy();
}
