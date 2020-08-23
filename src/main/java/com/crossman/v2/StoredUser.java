package com.crossman.v2;

import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class StoredUser {
	long id;
	String name;
	boolean active;
	boolean admin;
	ZonedDateTime created;

	public User toUser() {
		return new User(name, active, admin);
	}
}
