package com.calculatorify.service.notation;

/**
 * @author Anton Gorokh
 */
public enum NotationErrors {

	EXPRESSION_IS_BLANK,
	VALENCE_REACHED_ZERO,
	VALENCE_MORE_THAN_ONE,
	PARENTHESES_DONT_MATCH,
	COMMA_OUTSIDE_OF_FUNCTION_CONTEXT,
	FAILED_TO_PARSE_NUMBER; // number

	public String key() {
		return "ERROR.NOTATION." + this.name().replaceAll("__", ".");
	}
}
