package com.crossman;

import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
public class StoredOrder {
	long id;
	String username;
	Address address;
	List<Cheese> cheeses;
	BigDecimal price;
	boolean shipped;
}
