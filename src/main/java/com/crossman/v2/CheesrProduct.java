package com.crossman.v2;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

@Value
public class CheesrProduct implements Serializable {
	Type type;
	String name;
	BigDecimal price;

	public static enum Type {
		CHEESE
	}
}
