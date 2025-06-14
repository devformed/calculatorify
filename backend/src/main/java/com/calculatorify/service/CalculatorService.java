package com.calculatorify.service;

import com.calculatorify.exception.HttpHandlerException;
import com.calculatorify.model.dto.calculator.CalculatorDto;
import com.calculatorify.model.dto.calculator.CalculatorEnrichedEntry;
import com.calculatorify.model.dto.calculator.CalculatorEntry;
import com.calculatorify.model.dto.calculator.config.CalculatorConfig;
import com.calculatorify.model.dto.calculator.config.CalculatorOutput;
import com.calculatorify.model.dto.http.HttpPathContext;
import com.calculatorify.model.dto.http.HttpResponse;
import com.calculatorify.model.dto.session.SessionEntry;
import com.calculatorify.model.repository.calculator.CalculatorRepository;
import com.calculatorify.model.repository.session.SessionRepository;
import com.calculatorify.service.notation.ShuntingYardConverter;
import com.calculatorify.service.notation.token.Token;
import com.calculatorify.service.notation.token.Tokenizer;
import com.calculatorify.util.Json;
import com.calculatorify.util.http.HttpConstants;
import com.calculatorify.util.http.HttpHeaders;
import com.calculatorify.util.http.HttpUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Instant;
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
	private final SessionRepository sessionRepository;

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

	public HttpResponse updateCalculator(HttpExchange exchange, HttpPathContext context) throws java.io.IOException {
		UUID id = UUID.fromString(context.getPathVariable("id"));
		CalculatorEntry existing = repository.findById(id).orElseThrow();

		// authorize: only owner can update
		String sessionId = HttpUtils.getSessionId(exchange)
				.orElseThrow(() -> new HttpHandlerException(401, "Unauthorized"));
		SessionEntry sessionEntry = sessionRepository.findById(UUID.fromString(sessionId))
				.orElseThrow(() -> new HttpHandlerException(401, "Invalid session"));
		if (!sessionEntry.getUserId().equals(existing.getUserId())) {
			throw new HttpHandlerException(403, "Only author can modify this calculator");
		}

		// parse request body for new config and description
		String body = HttpUtils.getRequestBody(exchange);
		CalculatorDto dto = Json.fromJson(body, CalculatorDto.class);

		existing.setUpdatedAt(Instant.now());
		existing.setTitle(dto.getTitle());
		existing.setDescription(dto.getDescription());
		existing.setConfig(dto.getConfig());
		repository.merge(existing);
		return HttpResponse.ok();
	}

	public HttpResponse deleteCalculator(HttpExchange exchange, HttpPathContext context) {
		UUID id = UUID.fromString(context.getPathVariable("id"));
		CalculatorEntry existing = repository.findById(id)
				.orElseThrow(() -> new HttpHandlerException(404, "Calculator not found: %s".formatted(id)));

		// authorize: only owner can delete
		String sessionId = HttpUtils.getSessionId(exchange).orElseThrow();
		SessionEntry sessionEntry = sessionRepository.findById(UUID.fromString(sessionId)).orElseThrow();
		if (!sessionEntry.getUserId().equals(existing.getUserId())) {
			throw new HttpHandlerException(403, "Only author can delete this calculator");
		}

		repository.delete(id);
		return HttpResponse.ok();
	}

	public HttpResponse construct(HttpExchange exchange, HttpPathContext context) throws Exception {
		String sessionId = HttpUtils.getSessionId(exchange).orElseThrow();
		SessionEntry sessionEntry = sessionRepository.findById(UUID.fromString(sessionId)).orElseThrow();

		String prompt = context.getRequestParam("query");
		String aiBaseUrl = System.getenv().getOrDefault("BACKEND_AI_URL", "http://localhost:8000");
		Map<String, String> bodyMap = Map.of("message", prompt);
		String jsonBody = Json.toJsonSneaky(bodyMap);
		HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(URI.create(aiBaseUrl + "/chat"))
				.header(HttpHeaders.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_APPLICATION_JSON)
				.POST(HttpRequest.BodyPublishers.ofString(jsonBody))
				.build();
		HttpClient client = HttpClient.newHttpClient();
		int failures = 0;
		while (true) {
			java.net.http.HttpResponse<String> response = client.send(
					httpRequest,
					java.net.http.HttpResponse.BodyHandlers.ofString()
			);
			try {
				JsonNode node = Json.readTree(response.body());
				String title = node.get("title").textValue();
				String description = node.get("description").textValue();
				CalculatorConfig config = Json.toValue(node.get("config"), CalculatorConfig.class);
				return HttpResponse.ok(CalculatorEntry.builder()
						.title(title)
						.description(description)
						.config(config)
						.createdAt(Instant.now())
						.updatedAt(Instant.now())
						.userId(sessionEntry.getUserId())
						.build());
			} catch (Exception e) {
				failures++;
				if (failures >= 3) {
					throw new HttpHandlerException(
							500,
							"Failed to get a valid response from backend-ai after 3 attempts"
					);
				}
			}
		}
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
