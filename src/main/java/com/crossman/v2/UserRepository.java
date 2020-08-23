package com.crossman.v2;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
	public List<StoredUser> findAll();
	public Optional<StoredUser> findByName(String name);
}
