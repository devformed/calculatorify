package com.calculatorify.model.repository.user;

import com.calculatorify.model.dto.user.UserDto;
import com.calculatorify.model.dto.user.UserEntry;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Anton Gorokh
 */
public interface UserRepository {

	UUID persist(UserDto user);

	void merge(UserEntry user);

	Optional<UserEntry> findByUsername(String username);
}
