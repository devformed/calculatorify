package com.calculatorify.model.dto.calculator;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author Anton Gorokh
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class CalculatorEntry {

    @NotNull
    private UUID id;

    @NotNull
    private String title;

    @Nullable
    private String description;

    @NotNull
    private CalculatorConfig config;
}