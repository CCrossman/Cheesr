package com.crossman;

import lombok.Value;

import java.io.Serializable;

@Value
public class Address implements Serializable {
	Type kind;
	String name;
	String street;
	String city;
	String state;
	String zip;

	public boolean isNil() {
		return kind == null && name == null && street == null && city == null && state == null && zip == null;
	}

	public static enum Type {
		HOME, WORK
	}
}
