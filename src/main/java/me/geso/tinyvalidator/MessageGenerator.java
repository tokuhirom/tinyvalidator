package me.geso.tinyvalidator;

public interface MessageGenerator {
	public <T> String generateMessage(Violation<T> violation);
}
