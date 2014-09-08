package me.geso.tinyvalidator;

public interface MessageGenerator {
	public <T> String generateMessage(ConstraintViolation<T> violation);
}
