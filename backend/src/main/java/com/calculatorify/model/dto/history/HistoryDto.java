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
@AllArgsConstructor
@Builder(builderMethodName = "dtoBuilder")
public class HistoryDto {

	@NotNull
	private UUID userId;

	@NotNull
	private UUID calculatorId;

	@NotNull
	private String calculatorTitle;

	@NotNull
	private Instant accessedAt;
}