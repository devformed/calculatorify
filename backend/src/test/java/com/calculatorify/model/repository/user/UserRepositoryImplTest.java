package com.calculatorify.model.repository.user;

import com.calculatorify.model.dto.user.UserDto;
import com.calculatorify.model.dto.user.UserEntry;
import com.calculatorify.model.repository.AbstractTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
public class UserRepositoryImplTest extends AbstractTest {

    private final UserRepository userRepository = new UserRepositoryImpl();

    @Test
	public void testFindNotFound() {
		Optional<UserEntry> result = userRepository.findByUsername("nonexistent");
		assertTrue(result.isEmpty());
	}

	@Test
	public void testPersistAndFind() {
		UserDto user = new UserDto("john", "pass");
		UUID id = userRepository.persist(user);
		assertNotNull(id);

		Optional<UserEntry> entryOpt = userRepository.findByUsername("john");
		assertTrue(entryOpt.isPresent());
		UserEntry entry = entryOpt.get();
		assertEquals(id, entry.getId());
		assertEquals("john", entry.getUsername());
		assertEquals("pass", entry.getPassword());
	}

	@Test
	public void testMerge() {
		UserDto user = new UserDto("alice", "pw1");
		UUID id = userRepository.persist(user);

		Optional<UserEntry> entryOpt = userRepository.findByUsername("alice");
		assertTrue(entryOpt.isPresent());
		UserEntry entry = entryOpt.get();
		entry.setUsername("alice2");
		entry.setPassword("pw2");
		userRepository.merge(entry);

		Optional<UserEntry> updatedOpt = userRepository.findByUsername("alice2");
		assertTrue(updatedOpt.isPresent());
		UserEntry updated = updatedOpt.get();
		assertEquals(id, updated.getId());
		assertEquals("alice2", updated.getUsername());
		assertEquals("pw2", updated.getPassword());
	}

}