package com.crossman;

import lombok.Value;
import org.apache.wicket.Component;

@Value
public class Message {
	Level level;
	String value;

	public void apply(Component component) {
		if (getLevel() == Level.INFO) {
			component.info(value);
		} else if (getLevel() == Level.WARN) {
			component.warn(value);
		} else if (getLevel() == Level.ERROR) {
			component.error(value);
		} else {
			System.err.println("[" + level.name() + "] " + value);
		}
	}

	static enum Level {
		INFO, WARN, ERROR
	}

	public static Message info(String value) {
		return new Message(Level.INFO, value);
	}

	public static Message warn(String value) {
		return new Message(Level.WARN, value);
	}

	public static Message error(String value) {
		return new Message(Level.ERROR, value);
	}

	public static Message error(Exception ex) {
		return error(ex.getLocalizedMessage());
	}
}
