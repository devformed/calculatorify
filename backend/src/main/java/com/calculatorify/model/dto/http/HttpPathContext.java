package com.calculatorify.model.dto.http;

import java.util.Map;

/**
 * @author Anton Gorokh
 */
public record HttpPathContext(
		Map<String, String> pathVariables,
		Map<String, String> queryParameters
) {
}
