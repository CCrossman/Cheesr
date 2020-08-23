package com.crossman.v2;

import java.util.Optional;

public interface UserRepository {
	public Optional<StoredUser> findByName(String name);
}
