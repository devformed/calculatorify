package com.calculatorify.model.dto.history;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class HistoryEntry {
	@NotNull
	private UUID id;
	@NotNull
	private UUID userId;
	@NotNull
	private UUID calculatorId;
	@NotNull
	private Instant accessedAt;
}