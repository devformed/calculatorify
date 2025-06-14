package com.calculatorify.model.dto.calculator.config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Anton Gorokh
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
		@JsonSubTypes.Type(value = CalculatorInput.Slider.class),
		@JsonSubTypes.Type(value = CalculatorInput.RadioButtons.class),
})
public abstract class CalculatorInput {

	private @NotNull String type;
	private @NotNull String id;
	private @NotNull String name;

	@Getter
	@NoArgsConstructor
	@JsonTypeName("NUMBER")
	public static class Number extends CalculatorInput {

		private @NotNull BigDecimal number;
		private @NotNull Integer precision;

		public Number(String id, String name, BigDecimal number, Integer precision) {
			super("NUMBER", id, name);
			this.number = number;
			this.precision = precision;
		}
	}

	@Getter
	@NoArgsConstructor
	@JsonTypeName("SLIDER")
	public static class Slider extends CalculatorInput {

		private @NotNull BigDecimal minValue;
		private @NotNull BigDecimal maxValue;
		private @NotNull BigDecimal step;

		public Slider(String id, String name, BigDecimal minValue, BigDecimal maxValue, BigDecimal step) {
			super("SLIDER", id, name);
			this.minValue = minValue;
			this.maxValue = maxValue;
			this.step = step;
		}
	}

	@Getter
	@NoArgsConstructor
	@JsonTypeName("RADIO_BUTTONS")
	public static class RadioButtons extends CalculatorInput {

		private @NotNull Map<String, BigDecimal> nameValueOptions;

		public RadioButtons(String id, String name, Map<String, BigDecimal> nameValueOptions) {
			super("RADIO_BUTTONS", id, name);
			this.nameValueOptions = nameValueOptions;
		}
	}
}
