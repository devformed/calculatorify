package com.calculatorify.controller;

import com.calculatorify.exception.HttpHandlerException;
import com.calculatorify.model.dto.http.HttpMethod;
import com.calculatorify.model.dto.http.HttpResponse;
import com.calculatorify.model.repository.TransactionContext;
import com.calculatorify.service.HttpContextMatcher;
import com.calculatorify.service.HttpRequestHandler;
import com.calculatorify.util.http.HttpHeaders;
import com.google.common.collect.ImmutableMap;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

import static com.calculatorify.util.Json.toJsonSneaky;
import static com.calculatorify.util.Utils.nn;

/**
 * @author Anton Gorokh
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractController implements HttpHandler {

	private final SessionManager sessionManager;
	private final ImmutableMap<HttpContextMatcher, HttpRequestHandler> handlers;

	@Override
	public final void handle(HttpExchange exchange) throws IOException {
		log.info("Incoming request {} {}", exchange.getRequestMethod(), exchange.getRequestURI());
		try {
			addCorsHeaders(exchange);
			if (HttpMethod.OPTIONS.is(exchange.getRequestMethod())) {
				exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
				return;
			}
            // Wrap handler execution in transaction to ensure connections are closed
            HttpResponse response = TransactionContext.inTransaction(() -> {
                // Skip session filter for login and register endpoints
                String contextPath = exchange.getHttpContext().getPath();
                if (!"/login".equals(contextPath) && !"/register".equals(contextPath)) {
                    sessionManager.requestFilter(exchange);
                }
                HttpContextMatcher matcher = handlers.keySet().stream()
                    .filter(m -> m.match(HttpMethod.of(exchange.getRequestMethod()), exchange.getRequestURI()))
                    .findAny()
                    .orElseThrow(() -> new HttpHandlerException(404, "Not Found: %s %s".formatted(exchange.getRequestMethod(), exchange.getRequestURI())));
                return nn(handlers.get(matcher)).handle(exchange, matcher.parse(exchange.getRequestURI()));
            });
			sendResponse(exchange, response);
		} catch (HttpHandlerException e) {
			log.warn("{} {}", e.getStatusCode(), e.getMessage());
			sendResponse(exchange, HttpResponse.text(e.getStatusCode(), e.getMessage()));
		} catch (IllegalArgumentException | IllegalStateException | UnsupportedOperationException | NoSuchElementException e) {
			log.warn("{}", e.getMessage());
			sendResponse(exchange, HttpResponse.text(HttpURLConnection.HTTP_BAD_REQUEST, e.getMessage()));
		} catch (Exception e) {
			log.error("{}", e.getMessage());
			sendResponse(exchange, HttpResponse.text(HttpURLConnection.HTTP_INTERNAL_ERROR, "Internal Server Error: %s".formatted(e.getMessage())));
		} finally {
			exchange.close();
		}
	}

	private void sendResponse(HttpExchange exchange, HttpResponse response) throws IOException {
		if (response == null || response.content() == null) {
			exchange.sendResponseHeaders(response.statusCode(), -1);
			return;
		}
		byte[] content = switch (response.content()) {
			case byte[] bytes -> bytes;
			case String str -> str.getBytes(StandardCharsets.UTF_8);
			default -> toJsonSneaky(response.content()).getBytes(StandardCharsets.UTF_8);
		};
		exchange.getResponseHeaders().set(HttpHeaders.CONTENT_TYPE, response.contentType());
		exchange.sendResponseHeaders(response.statusCode(), content.length);
		try (OutputStream os = exchange.getResponseBody()) {
			os.write(content);
		}
	}

	private void addCorsHeaders(HttpExchange exchange) {
       String origin = exchange.getRequestHeaders().getFirst(HttpHeaders.ORIGIN);
       if (origin == null) {
           exchange.getResponseHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
           return;
       }
       exchange.getResponseHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
       exchange.getResponseHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
       // Support preflight and actual request with JSON content
       exchange.getResponseHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "OPTIONS,GET,POST,PUT,DELETE,PATCH");
       exchange.getResponseHeaders().add(
           HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
           "Origin,Content-Type,Accept,Authorization,Access-Control-Request-Method,Access-Control-Request-Headers"
       );
	}
}
