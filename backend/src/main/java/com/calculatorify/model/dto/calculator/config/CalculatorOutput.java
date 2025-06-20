package com.calculatorify.model.dto.calculator.config;

/**
 * @author Anton Gorokh
 */
public record CalculatorOutput(
		String name,
		String formula,
		Integer precision,
		Integer order
) {
}
