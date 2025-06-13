package com.calculatorify.model.repository.session;

import com.calculatorify.model.dto.session.SessionDto;
import com.calculatorify.model.dto.session.SessionEntry;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository {
    UUID persist(SessionDto session);
    void merge(SessionEntry session);
    Optional<SessionEntry> findById(UUID id);
    void delete(UUID id);
    void deleteByAccessedAtOlderThan(Instant time);
}