package com.calculatorify.service;

import com.calculatorify.model.dto.http.HttpPathContext;
import com.calculatorify.model.dto.http.HttpResponse;
import com.sun.net.httpserver.HttpExchange;

import java.util.List;

/**
 * @author Anton Gorokh
 */
public class CalculatorService {

	public HttpResponse getCalculators(HttpExchange exchange, HttpPathContext context) {
		var fullText = context.getRequestParam("q");
		return HttpResponse.ok(List.of());
	}
}
