package com.calculatorify.service;

import com.calculatorify.controller.SessionManager;
import com.calculatorify.exception.HttpHandlerException;
import com.calculatorify.model.dto.calculator.CalculatorEntry;
import com.calculatorify.model.dto.history.HistoryDto;
import com.calculatorify.model.dto.history.HistoryEntry;
import com.calculatorify.model.dto.http.HttpPathContext;
import com.calculatorify.model.dto.http.HttpResponse;
import com.calculatorify.model.dto.session.SessionEntry;
import com.calculatorify.model.dto.user.UserEntry;
import com.calculatorify.model.repository.history.HistoryRepository;
import com.calculatorify.util.Json;
import com.calculatorify.util.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * @author Anton Gorokh
 */
@RequiredArgsConstructor
public class HistoryService {

	private final HistoryRepository historyRepository;
	private final SessionManager sessionManager;

	public void upsertHistory(UUID userId, CalculatorEntry entry) {
		if (entry.getId() == null) {
			return;
		}
		historyRepository.findByUserIdAndCalculatorId(userId, entry.getId())
				.ifPresentOrElse(this::updateHistory, () -> insertHistory(userId, entry));
	}

	public HttpResponse findAll(HttpExchange exchange, HttpPathContext context) throws IOException {
		UserEntry user = sessionManager.getSessionUser(exchange);
		List<HistoryEntry> history = historyRepository.findByUserId(user.getId());
		return HttpResponse.ok(history);
	}

	private void updateHistory(HistoryEntry history) {
		history.setAccessedAt(Instant.now());
	}

	private void insertHistory(UUID userId, CalculatorEntry entry) {
		historyRepository.persist(HistoryDto.builder()
				.userId(userId)
				.calculatorId(entry.getId())
				.accessedAt(entry.getUpdatedAt())
				.build());
	}
}
