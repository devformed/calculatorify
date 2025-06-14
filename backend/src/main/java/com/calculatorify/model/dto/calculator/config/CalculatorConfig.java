package com.calculatorify.model.dto.calculator.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Anton Gorokh
 */
@Getter
@Setter
public class CalculatorConfig {

	private @NotEmpty List<CalculatorInput> inputs;

	private @NotEmpty List<CalculatorOutput> outputs;
}
