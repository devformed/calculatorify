package com.calculatorify.model.repository.history;

import com.calculatorify.model.dto.history.HistoryDto;
import com.calculatorify.model.dto.history.HistoryEntry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HistoryRepository {

	UUID persist(HistoryDto history);

	void merge(HistoryEntry history);

	Optional<HistoryEntry> findByUserIdAndCalculatorId(UUID userId, UUID calculatorId);

	List<HistoryEntry> findByUserId(UUID userId);
}