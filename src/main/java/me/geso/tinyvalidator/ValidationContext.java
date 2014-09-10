package me.geso.tinyvalidator;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Created by tokuhirom on 9/10/14.
 */
class ValidationContext<T> {
    private final List<ConstraintViolation<T>> violations = new ArrayList<>();
    private final Set<Object> seen = new HashSet<>();
    private final Node node = new Node();
    private final T rootObject;
    private Object route;

    public ValidationContext(T bean) {
        this.rootObject = bean;
    }

    public List<ConstraintViolation<T>> getViolations() {
        return Collections.unmodifiableList(violations);
    }

    public void registerSeen(Object target) {
        this.seen.add(target);
    }

    public Object getRoute() {
        return route;
    }

    public boolean isSeen(Object fieldValue) {
        return this.seen.contains(fieldValue);
    }

    public Object getRootObject() {
        return rootObject;
    }

    public void addViolation(Annotation annotation, Object target, Node node) {
        ConstraintViolation<T> violation = new ConstraintViolation<T>(
                rootObject,
                target,
                annotation,
                node);
        this.violations.add(violation);
    }
}
