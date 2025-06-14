package com.calculatorify.model.dto.calculator;

import com.calculatorify.model.dto.calculator.config.CalculatorConfig;
import com.calculatorify.service.notation.token.Token;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Anton Gorokh
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalculatorEnrichedEntry {

    @NotNull
    private UUID id;

    @NotNull
    private String title;

    @Nullable
    private String description;

    @NotNull
    private CalculatorConfig config;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    @NotNull
    private UUID userId;

    @NotNull
    private Map<String, List<Token>> outputNameToPostfixFormula;
}