package com.calculatorify.util.http;

import com.sun.net.httpserver.HttpExchange;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.HttpCookie;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;

/**
 * @author Anton Gorokh
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpUtils {

	public static Optional<String> getSessionId(HttpExchange exchange) {
		return Optional.of(exchange.getRequestHeaders())
				.map(headers -> headers.getFirst(HttpHeaders.COOKIE))
				.map(HttpCookie::parse)
				.stream()
				.flatMap(Collection::stream)
				.filter(c -> HttpConstants.COOKIE_SESSION_ID.equals(c.getName()))
				.map(HttpCookie::getValue)
				.findAny();
	}

	public static String getRequestBody(HttpExchange exchange) throws IOException {
		try (var is = exchange.getRequestBody()) {
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
}
