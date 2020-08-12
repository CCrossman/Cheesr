package com.crossman;

import lombok.Value;

@Value
public class Address {
	String name;
	String street;
	String city;
	String state;
	String zipCode;
}
