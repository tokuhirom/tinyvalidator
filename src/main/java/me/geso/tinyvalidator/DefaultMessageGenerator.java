package me.geso.tinyvalidator;

import me.geso.tinyvalidator.constraints.NotNull;
import me.geso.tinyvalidator.constraints.Pattern;
import me.geso.tinyvalidator.constraints.Size;

public class DefaultMessageGenerator implements MessageGenerator {
	public <T> String generateMessage(Violation<T> violation) {
		if (violation.getAnnotation().annotationType() == NotNull.class) {
			return String.format("You should fill %s.", violation.getRoutePath("."));
		} else if (violation.getAnnotation().annotationType() == Pattern.class) {
			Pattern pattern = (Pattern)violation.getAnnotation();
			return String.format("%s does not matches %s.", violation.getRoutePath("."), pattern.regexp());
		} else if (violation.getAnnotation().annotationType() == Size.class) {
			Size size = (Size)violation.getAnnotation();
			return String.format("%s: %s < %s < %s", violation.getRoutePath("."), size.min(), violation.getFieldValue(), size.max());
		} else {
			throw new RuntimeException("Unknown message type: " + violation.getAnnotation());
		}
	}
}
