package com.calculatorify.service.notation.token;

import lombok.Getter;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Anton Gorokh
 */
@Getter
public enum TokenType {
	DECIMAL_LITERAL("-?\\d+(\\.\\d+)?"),
	BOOLEAN_LITERAL("(true|false)"),

	VARIABLE("\\$\\{(.+?)\\}"),
	OPERATOR("[+\\-*/^%]"),
	LEFT_PAREN("\\("),
	RIGHT_PAREN("\\)"),
	FUNCTION(toRegExp(Func.values())),
	COMMA(","),
	STRING_LITERAL("[a-zA-Z ]+");

	private final Pattern pattern;

	TokenType(String regExp) {
		this.pattern = Pattern.compile(regExp);
	}

	public boolean matches(String value) {
		return pattern.matcher(value).matches();
	}

	private static String toRegExp(Enum<?>[] values) {
		return "(" + Arrays.stream(values).map(Enum::name).collect(Collectors.joining("|")) + ")";
	}
}
