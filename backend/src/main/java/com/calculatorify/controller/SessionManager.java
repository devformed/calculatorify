package com.calculatorify.controller;

import com.calculatorify.exception.HttpHandlerException;
import com.calculatorify.model.dto.session.SessionDto;
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

	private final SessionRepository sessionRepo;

	public void requestFilter(HttpExchange exchange) {
		Optional<SessionDto> session = HttpUtils.getSessionId(exchange)
				.map(UUID::fromString)
				.flatMap(sessionRepo::findById)
				.map(sessionDto -> sessionDto.setAccessedAt(Instant.now()));

		if (session.isEmpty()) {
			throw new HttpHandlerException(401, "Unauthorized access: SESSIONID not found");
		}
		sessionRepo.merge(session.get());
	}

	public String createSession(UUID userId) {
		Instant now = Instant.now();
		SessionDto session = SessionDto.builder()
				.userId(userId)
				.accessedAt(now)
				.build();
		UUID sessionId = sessionRepo.persist(session);
		return sessionId.toString();
	}

	public void invalidateSession(String sessionId) {
		if (sessionId == null) {
			return;
		}
		try {
			UUID id = UUID.fromString(sessionId);
			sessionRepo.delete(id);
		} catch (IllegalArgumentException e) {
			// ignore invalid format
		}
	}

	public boolean valid(String sessionId) {
		if (sessionId == null) {
			return false;
		}
		UUID id = UUID.fromString(sessionId);
		Optional<SessionDto> sessionOpt = sessionRepo.findById(id);
		if (sessionOpt.isEmpty()) {
			return false;
		}
		SessionDto session = sessionOpt.get();
		SessionDto updated = SessionDto.builder()
				.id(session.getId())
				.userId(session.getUserId())
				.accessedAt(Instant.now())
				.build();
		sessionRepo.merge(updated);
		return true;
	}
}