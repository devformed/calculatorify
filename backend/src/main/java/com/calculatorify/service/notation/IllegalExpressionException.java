package com.calculatorify.service.notation;

/**
 * @author Anton Gorokh
 */
public class IllegalExpressionException extends IllegalArgumentException {

	public IllegalExpressionException(String message) {
		super(message);
	}

	public IllegalExpressionException(NotationErrors error) {
		super(error.name());
	}
}
