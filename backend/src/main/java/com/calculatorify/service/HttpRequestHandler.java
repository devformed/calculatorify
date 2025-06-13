package com.calculatorify.service;

import com.calculatorify.model.dto.http.HttpPathContext;
import com.calculatorify.model.dto.http.HttpResponse;
import com.sun.net.httpserver.HttpExchange;

/**
 * @author Anton Gorokh
 */
@FunctionalInterface
public interface HttpRequestHandler {
	HttpResponse handle(HttpExchange exchange, HttpPathContext context) throws Exception;
}
