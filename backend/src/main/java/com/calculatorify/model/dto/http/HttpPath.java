package com.calculatorify.model.dto.http;

import java.net.URI;

/**
 * @author Anton Gorokh
 */
public record HttpPath(HttpMethod method, URI path) {

	public static HttpPath of(String method, URI path) {
		return new HttpPath(HttpMethod.valueOf(method.toUpperCase()), path);
	}
}
