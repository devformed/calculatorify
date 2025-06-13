package com.calculatorify.model.dto.http;

import java.util.Map;

/**
 * @author Anton Gorokh
 */
public record HttpPathContext(
		Map<String, String> pathVariables,
		Map<String, String> requestParams
) {

	public String getPathVariable(String name) {
		return pathVariables.get(name);
	}

	public String getRequestParam(String name) {
		return requestParams.get(name);
	}
}
