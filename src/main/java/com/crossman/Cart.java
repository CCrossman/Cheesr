package com.crossman;

import com.crossman.v2.CheesrProduct;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class Cart implements Serializable {
	@Getter
	@Setter
	private List<CheesrProduct> products = new ArrayList<>();

	public BigDecimal getTotal() {
		BigDecimal total = BigDecimal.ZERO;
		if (products != null) {
			for (CheesrProduct product : products) {
				total = total.add(product.getPrice());
			}
		}
		return total;
	}
}
