package com.crossman.v1;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Value
public class StoredOrder implements Serializable {
	long id;
	String username;
	Address address;
	List<Cheese> cheeses;
	BigDecimal price;
	boolean shipped;
}
