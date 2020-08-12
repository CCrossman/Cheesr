package com.crossman;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

@Value
public class Cheese implements Serializable {
	String name;
	BigDecimal price;
}
