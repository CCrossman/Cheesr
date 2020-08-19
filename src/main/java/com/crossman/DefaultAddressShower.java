package com.crossman;

public enum DefaultAddressShower implements Show<Address> {
	instance;

	@Override
	public String show(Address address) {
		return address.getStreet() + ", " + address.getCity() + ", " + address.getState() + ", " + address.getZip();
	}
}
