package com.calculatorify.model.repository.session;

import com.calculatorify.model.dto.session.SessionDto;
import com.calculatorify.model.dto.session.SessionEntry;
import com.calculatorify.model.repository.AbstractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for SessionRepositoryImpl.
 */
@TestInstance(Lifecycle.PER_CLASS)
public class SessionRepositoryImplTest extends AbstractTest {

    private final SessionRepository repo = new SessionRepositoryImpl();

    @BeforeEach
    public void preconditions() {
        // No sessions exist at test start
        Optional<SessionEntry> random = repo.findById(UUID.randomUUID());
        assertTrue(random.isEmpty());
    }

    @Test
    public void testFindNotFound() {
        Optional<SessionEntry> result = repo.findById(UUID.randomUUID());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testPersistAndFind() {
        Instant now = Instant.now();
        SessionDto session = SessionDto.builder()
            .userId(TEST_UUID)
            .accessedAt(now)
            .build();
        UUID id = repo.persist(session);
        assertNotNull(id);

        Optional<SessionEntry> foundOpt = repo.findById(id);
        assertTrue(foundOpt.isPresent());
        SessionEntry found = foundOpt.get();
        assertEquals(id, found.getId());
        assertEquals(session.getUserId(), found.getUserId());
        assertEquals(now, found.getAccessedAt());
    }

    @Test
    public void testMerge() {
        Instant now = Instant.now();
        SessionDto session = SessionDto.builder()
            .userId(TEST_UUID)
            .accessedAt(now)
            .build();
        UUID id = repo.persist(session);
        Optional<SessionEntry> foundOpt = repo.findById(id);
        assertTrue(foundOpt.isPresent());
        SessionEntry found = foundOpt.get();

        Instant newTime = now.plusSeconds(3600);
        found.setAccessedAt(newTime);
        repo.merge(found);

        Optional<SessionEntry> updatedOpt = repo.findById(id);
        assertTrue(updatedOpt.isPresent());
        SessionEntry up = updatedOpt.get();
        assertEquals(id, up.getId());
        assertEquals(found.getUserId(), up.getUserId());
        assertEquals(newTime, up.getAccessedAt());
    }

    @Test
    public void testDelete() {
        Instant now = Instant.now();
        SessionDto session = SessionDto.builder()
            .userId(TEST_UUID)
            .accessedAt(now)
            .build();
        UUID id = repo.persist(session);
        Optional<SessionEntry> foundOpt = repo.findById(id);
        assertTrue(foundOpt.isPresent());

        repo.delete(id);
        Optional<SessionEntry> afterOpt = repo.findById(id);
        assertTrue(afterOpt.isEmpty());
    }
    
    @Test
    public void testDeleteByAccessedAtOlderThan() {
        Instant now = Instant.now();
        Instant older = now.minusSeconds(3600);
        Instant newer = now.minusSeconds(60);
        // create older session
        SessionDto oldSession = SessionDto.builder()
            .userId(TEST_UUID)
            .accessedAt(older)
            .build();
        UUID oldId = repo.persist(oldSession);
        // create newer session
        SessionDto newSession = SessionDto.builder()
            .userId(TEST_UUID)
            .accessedAt(newer)
            .build();
        UUID newId = repo.persist(newSession);
        // delete sessions older than threshold (now - 1800s)
        Instant threshold = now.minusSeconds(1800);
        repo.deleteByAccessedAtOlderThan(threshold);
        // old session should be removed
        assertTrue(repo.findById(oldId).isEmpty());
        // new session should remain
        Optional<SessionEntry> newOpt = repo.findById(newId);
        assertTrue(newOpt.isPresent());
        assertEquals(newId, newOpt.get().getId());
    }
}