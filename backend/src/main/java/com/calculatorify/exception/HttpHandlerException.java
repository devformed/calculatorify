package com.calculatorify.exception;

import lombok.Getter;

@Getter
public class HttpHandlerException extends IllegalArgumentException {

	private final int statusCode;

	public HttpHandlerException(int statusCode, String s) {
		super(s);
		this.statusCode = statusCode;
	}
}
