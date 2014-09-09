package me.geso.tinyvalidator;

import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConstraintViolation<T> {
    @Override
    public String toString() {
        return "ConstraintViolation [object=" + object + ", annotation="
                + annotation + ", route=" + route + ", fieldValue="
                + fieldValue + "]";
    }

    private static final Pattern pattern = Pattern.compile("\\{([^}]+)\\}");

    private final T object;
    private final Annotation annotation;
    private final Node route;
    private final Object fieldValue;

    public ConstraintViolation(T object, Object fieldValue,
                               Annotation annotation, Node route) {
        this.object = object;
        this.fieldValue = fieldValue;
        this.annotation = annotation;
        this.route = route;
    }

    public T getObject() {
        return object;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public Node getPropertyPath() {
        return route;
    }

    @SneakyThrows
    public String getMessage() {
        // ref.
        // https://svn.apache.org/repos/asf/tapestry/tapestry5/trunk/tapestry-beanvalidator/src/test/resources/ValidationMessages_en.properties
        Method method = annotation.getClass()
                .getMethod("message");
        String messageTemplate = (String) method.invoke(annotation);
        StringBuffer resultString = new StringBuffer();
        final Matcher matcher = pattern.matcher(messageTemplate);
        while (matcher.find()) {
            final MatchResult matchResult = matcher.toMatchResult();
            final String methodName = matchResult.group(1);
            final String value = annotation.getClass()
                    .getMethod(methodName).invoke(annotation)
                    .toString();
            matcher.appendReplacement(resultString,
                    Matcher.quoteReplacement(value));
        }
        matcher.appendTail(resultString);
        return resultString.toString();
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
