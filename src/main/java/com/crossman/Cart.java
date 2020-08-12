package com.crossman;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class Cart implements Serializable {
	@Getter
	@Setter
	private List<Cheese> cheeses = new ArrayList<>();

	@Getter
	@Setter
	private Address billingAddress = new Address(null, null, null, null, null);

	public BigDecimal getTotal() {
		BigDecimal total = BigDecimal.ZERO;
		if (cheeses != null) {
			for (Cheese cheese : cheeses) {
				total = total.add(cheese.getPrice());
			}
		}
		return total;
	}
}
