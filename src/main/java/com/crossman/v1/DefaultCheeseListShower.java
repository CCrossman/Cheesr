package com.crossman.v1;

import com.crossman.Show;

import java.util.List;
import java.util.stream.Collectors;

public enum DefaultCheeseListShower implements Show<List<Cheese>> {
	instance;

	@Override
	public String show(List<Cheese> cheeses) {
		return cheeses.stream().map(Cheese::getName).collect(Collectors.joining(", "));
	}
}
