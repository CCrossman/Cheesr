package com.crossman;

import lombok.Value;

import java.io.Serializable;

@Value
public class Address implements Serializable {
	String name;
	String street;
	String city;
	String state;
	String zip;

	public boolean isNil() {
		return name == null && street == null && city == null && state == null && zip == null;
	}
}
