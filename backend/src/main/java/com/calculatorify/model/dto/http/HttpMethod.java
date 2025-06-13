package com.calculatorify.model.dto.http;

/**
 * @author Anton Gorokh
 */
public enum HttpMethod {

	GET,
	POST,
	PUT,
	PATCH,
	DELETE,
	HEAD,
	OPTIONS;

	public boolean is(String method) {
		return this.name().equalsIgnoreCase(method);
	}

	public static HttpMethod of(String method) {
		return HttpMethod.valueOf(method.toUpperCase());
	}
}
