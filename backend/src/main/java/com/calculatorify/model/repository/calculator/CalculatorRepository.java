package com.calculatorify.model.repository.calculator;

import com.calculatorify.model.dto.calculator.CalculatorDto;
import com.calculatorify.model.dto.calculator.CalculatorEntry;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CalculatorRepository {

    UUID persist(CalculatorDto calculator);

    void merge(CalculatorEntry calculator);

    Optional<CalculatorEntry> findById(UUID id);

    List<CalculatorEntry> findAllByFullText(String text);

    void delete(UUID id);
}