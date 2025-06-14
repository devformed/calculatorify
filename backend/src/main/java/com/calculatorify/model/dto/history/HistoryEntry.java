package com.calculatorify.model.dto.history;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class HistoryEntry extends HistoryDto {

	@NotNull
	private UUID id;

	@Builder
	public HistoryEntry(UUID userId, UUID calculatorId, String calculatorTitle, Instant accessedAt, UUID id) {
		super(userId, calculatorId, calculatorTitle, accessedAt);
		this.id = id;
	}
}