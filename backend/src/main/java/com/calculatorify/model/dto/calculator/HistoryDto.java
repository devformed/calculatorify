package com.calculatorify.model.dto.calculator;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Anton Gorokh
 */
public record HistoryDto(Instant accessedAt, UUID calculatorId, String calculatorTitle) {
}
