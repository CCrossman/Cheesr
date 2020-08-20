package com.crossman;

import com.crossman.v1.Cheese;
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
