package com.calculatorify.service;

import com.calculatorify.model.dto.calculator.CalculatorEnrichedEntry;
import com.calculatorify.model.dto.calculator.CalculatorEntry;
import com.calculatorify.model.dto.calculator.config.CalculatorOutput;
import com.calculatorify.model.dto.http.HttpPathContext;
import com.calculatorify.model.dto.http.HttpResponse;
import com.calculatorify.model.repository.calculator.CalculatorRepository;
import com.calculatorify.service.notation.ShuntingYardConverter;
import com.calculatorify.service.notation.token.Token;
import com.calculatorify.service.notation.token.Tokenizer;
import com.sun.net.httpserver.HttpExchange;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
		var entry = repository.findById(id).orElseThrow();
		return HttpResponse.ok(toEnrichedEntry(entry));
	}

	private Map<String, List<Token>> getOutputNameToPostfixFormula(CalculatorEntry entry) {
		return entry.getConfig()
				.getOutputs()
				.stream()
				.collect(Collectors.toMap(CalculatorOutput::name, output -> ShuntingYardConverter.toPostfix(Tokenizer.tokenize(output.formula()))));
	}

	private CalculatorEnrichedEntry toEnrichedEntry(CalculatorEntry entry) {
		return CalculatorEnrichedEntry.builder()
				.id(entry.getId())
				.title(entry.getTitle())
				.description(entry.getDescription())
				.config(entry.getConfig())
				.createdAt(entry.getCreatedAt())
				.updatedAt(entry.getUpdatedAt())
				.userId(entry.getUserId())
				.outputNameToPostfixFormula(getOutputNameToPostfixFormula(entry))
				.build();
	}
}
