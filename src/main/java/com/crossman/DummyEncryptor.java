package com.crossman;

public enum DummyEncryptor implements Encryptor {
	instance;

	@Override
	public String encrypt(String str) {
		return str;
	}
}
