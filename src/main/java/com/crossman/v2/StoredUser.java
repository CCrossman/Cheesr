package com.crossman.v2;

import lombok.Value;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Value
public class StoredUser implements Serializable {
	long id;
	String name;
	boolean active;
	boolean admin;
	ZonedDateTime created;
}
