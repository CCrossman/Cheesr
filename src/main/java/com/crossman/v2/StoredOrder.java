package com.crossman.v2;

import com.crossman.v1.Address;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Value
public class StoredOrder implements Serializable {
	long id;
	String username;
	Address address;
	List<CheesrProduct> products;
	BigDecimal price;
	boolean shipped;
}
