package me.geso.tinyvalidator;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Created by tokuhirom on 9/10/14.
 */
class ValidationContext {
    private final List<ConstraintViolation> violations = new ArrayList<>();
    private final Set<Object> seen = new HashSet<>();

    public List<ConstraintViolation> getViolations() {
        return Collections.unmodifiableList(violations);
    }

    public void registerSeen(Object target) {
        this.seen.add(target);
    }

    public boolean isSeen(Object fieldValue) {
        return this.seen.contains(fieldValue);
    }

    public void addViolation(ConstraintViolation violation) {
        this.violations.add(violation);
    }
}
