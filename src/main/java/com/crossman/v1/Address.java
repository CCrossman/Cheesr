package com.crossman.v1;

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

	public static enum Type {
		HOME, WORK
	}
}
