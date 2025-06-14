package com.calculatorify.model.dto.calculator;

import com.calculatorify.model.dto.calculator.config.CalculatorConfig;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Anton Gorokh
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class CalculatorDto {

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
}