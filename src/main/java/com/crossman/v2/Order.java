package com.crossman.v2;

import com.crossman.v1.Address;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
public class Order {
	Version version;
	String username;
	Address address;
	List<CheesrProduct> products;
	BigDecimal price;

	public static enum Version {
		v2
	}
}
