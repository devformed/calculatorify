package com.calculatorify.service.notation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Deque;

/**
 * @author Anton Gorokh
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotationResolverUtils {

	public static void addNumber(Deque<Object> stack, Number number) {
		if (number.doubleValue() % 1 == 0) {
			stack.add(number.intValue());
		} else {
			stack.add(number);
		}
	}

	public static Double popDouble(Deque<Object> stack) {
		var value = stack.pop();

		if (value instanceof Number number) {
			return number.doubleValue();
		}
		if (value instanceof Boolean bool) {
			return bool ? 1. : 0;
		}
		if (value instanceof String string) {
			return convertToDouble(string);
		}
		throw new IllegalStateException("Unexpected object in stack: " + value);
	}

	private static Double convertToDouble(String string) {
		if (NumberUtils.isParsable(string)) {
			return Double.parseDouble(string);
		}
		if (BooleanUtils.toBooleanObject(string) != null) {
			return BooleanUtils.toBoolean(string) ? 1. : 0;
		}
		throw new IllegalArgumentException("Failed to parse number");
	}
}
