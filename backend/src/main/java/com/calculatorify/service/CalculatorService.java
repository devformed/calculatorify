package com.calculatorify.service;

import com.calculatorify.model.dto.calculator.CalculatorEntry;
import com.calculatorify.model.dto.http.HttpPathContext;
import com.calculatorify.model.dto.http.HttpResponse;
import com.calculatorify.model.repository.calculator.CalculatorRepository;
import com.sun.net.httpserver.HttpExchange;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * @author Anton Gorokh
 */
@RequiredArgsConstructor
public class CalculatorService {

	private final CalculatorRepository repository;

	public HttpResponse getCalculators(HttpExchange exchange, HttpPathContext context) {
		var fullText = context.getRequestParam("q");
		List<CalculatorEntry> entries = repository.findAllByFullText(fullText);
		return HttpResponse.ok(entries);
	}

	public HttpResponse getCalculator(HttpExchange exchange, HttpPathContext context) {
		UUID id = UUID.fromString(context.getPathVariable("id"));
		CalculatorEntry entry = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Calculator not found"));
		return HttpResponse.ok(entry);
	}
}
