package com.crossman;

import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
public class Order {
	String username;
	Address address;
	List<Cheese> cheeses;
	BigDecimal price;
}
