package me.geso.tinyvalidator;

import java.util.*;

/**
 * Created by tokuhirom on 9/10/14.
 */
class ValidationContext {
    private final List<ConstraintViolation> violations = new ArrayList<>();
    private final Set<Object> seen = new HashSet<>();

    List<ConstraintViolation> getViolations() {
        return Collections.unmodifiableList(violations);
    }

    void registerSeen(Object target) {
        this.seen.add(target);
    }

    boolean isSeen(Object fieldValue) {
        return this.seen.contains(fieldValue);
    }

    void addViolation(ConstraintViolation violation) {
        this.violations.add(violation);
    }
}
