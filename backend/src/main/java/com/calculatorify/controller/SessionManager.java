package com.calculatorify.controller;

import com.calculatorify.exception.HttpHandlerException;
import com.calculatorify.model.dto.session.SessionDto;
import com.calculatorify.model.dto.session.SessionEntry;
import com.calculatorify.model.repository.session.SessionRepository;
import com.calculatorify.util.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Anton Gorokh
 */
@RequiredArgsConstructor
public final class SessionManager {

	private final SessionRepository repository;

	public void requestFilter(HttpExchange exchange) {
		Optional<SessionEntry> session = HttpUtils.getSessionId(exchange)
				.map(UUID::fromString)
				.flatMap(repository::findById)
				.map(sessionDto -> sessionDto.setAccessedAt(Instant.now()));

		if (session.isEmpty()) {
			throw new HttpHandlerException(401, "Unauthorized access: SESSIONID not found");
		}
		repository.merge(session.get());
	}

	public String createSession(UUID userId) {
		Instant now = Instant.now();
		SessionDto session = SessionDto.builder()
				.userId(userId)
				.accessedAt(now)
				.build();
		UUID sessionId = repository.persist(session);
		return sessionId.toString();
	}

	public void invalidateSession(String sessionId) {
		if (sessionId == null) {
			return;
		}
		try {
			UUID id = UUID.fromString(sessionId);
			repository.delete(id);
		} catch (IllegalArgumentException e) {
			// ignore invalid format
		}
	}
}