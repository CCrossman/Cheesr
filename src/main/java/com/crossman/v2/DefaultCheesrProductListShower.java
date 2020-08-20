package com.crossman.v2;

import com.crossman.Show;

import java.util.List;
import java.util.stream.Collectors;

public enum DefaultCheesrProductListShower implements Show<List<CheesrProduct>> {
	instance;

	@Override
	public String show(List<CheesrProduct> cheesrProducts) {
		return cheesrProducts.stream().map(CheesrProduct::getName).collect(Collectors.joining(", "));
	}
}
