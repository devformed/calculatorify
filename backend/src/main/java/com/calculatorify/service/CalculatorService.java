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
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sun.net.httpserver.HttpExchange;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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

	public HttpResponse create(HttpExchange exchange, HttpPathContext context) throws Exception {
		String sessionId = HttpUtils.getSessionId(exchange)
				.orElseThrow(() -> new HttpHandlerException(401, "Unauthorized"));
		SessionEntry sessionEntry = sessionRepository.findById(UUID.fromString(sessionId))
				.orElseThrow(() -> new HttpHandlerException(401, "Invalid session"));

		String body = HttpUtils.getRequestBody(exchange);
		CalculatorDto dto = Json.fromJson(body, CalculatorDto.class);
		dto.setCreatedAt(Instant.now());
		dto.setUpdatedAt(Instant.now());
		dto.setUserId(sessionEntry.getUserId());

		UUID id = repository.persist(dto);
		return HttpResponse.ok(id);
	}

	public HttpResponse construct(HttpExchange exchange, HttpPathContext context) throws Exception {
        // read prompt from JSON request body instead of query param
        String body = HttpUtils.getRequestBody(exchange);
        JsonNode json = Json.readTree(body);
        if (!json.has("prompt")) {
            throw new HttpHandlerException(400, "Missing prompt in request body");
        }
        String prompt = json.get("prompt").asText();

		String aiBaseUrl = System.getenv().getOrDefault("BACKEND_AI_URL", "http://localhost:8000");
		String systemMessage = System.getenv().getOrDefault("BACKEND_AI_SYSTEM_MESSAGE", LLM_SYSTEM_MSG);
		String payload = Json.toJson(new ChatRequest(systemMessage, prompt, "o4-mini"));

		// use OkHttp to forward request
		okhttp3.OkHttpClient client = new OkHttpClient.Builder()
				.connectTimeout(3, TimeUnit.MINUTES)
				.readTimeout(1, TimeUnit.MINUTES)
				.writeTimeout(1, TimeUnit.MINUTES)
				.callTimeout(1, TimeUnit.MINUTES)
				.build();
		okhttp3.MediaType mediaType = okhttp3.MediaType.parse(HttpConstants.CONTENT_TYPE_APPLICATION_JSON);
		okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(payload, mediaType);
		okhttp3.Request request = new okhttp3.Request.Builder()
				.url(aiBaseUrl + "/chat")
				.addHeader(HttpHeaders.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_APPLICATION_JSON)
				.post(requestBody)
				.build();
		try (okhttp3.Response response = client.newCall(request).execute()) {
			String respBody = response.body() != null ? response.body().string() : null;
			if (response.code() != 200) {
				throw new HttpHandlerException(
						response.code(),
						"Error from backend-ai: " + respBody
				);
			}
			CalculatorEnrichedEntry entry = Json.fromJson(Json.readTree(Json.readTree(respBody).get("response").asText()), CalculatorEnrichedEntry.class);
			entry.setOutputNameToPostfixFormula(getOutputNameToPostfixFormula(entry.getConfig()));
			return HttpResponse.ok(entry);
		}
	}

	private Map<String, List<Token>> getOutputNameToPostfixFormula(CalculatorConfig config) {
		return config
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
				.outputNameToPostfixFormula(getOutputNameToPostfixFormula(entry.getConfig()))
				.build();
	}

	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	private record ChatRequest(
			String systemMessage,
			String message,
			String model
	) {
	}

	// todo move to properties this ugly ahh constant
	private static final String LLM_SYSTEM_MSG = """
			<SYSTEM>
			You are “Calculator-Config-Generator”.
			Return ONE valid JSON object of the exact form
			{
			"title":        "<short Polish title>",
			"description":  "<longer Polish description or null>",
			"config": {
			"inputs":  [<CalculatorInput>, …],
			"outputs": [<CalculatorOutput>, …]
			}
			}
			
			Allowed input types and required fields. Prioritize slider inputs over number inputs.
			NUMBER
			{
			"type": "NUMBER",
			"id": "<snake_case_english_identifier>",
			"name": "<Polish label>",
			"order": <1-based integer>,
			"number": <default BigDecimal>,
			"precision": <non-negative integer>
			}
			SLIDER
			{
			"type": "SLIDER",
			"id": "<snake_case_english_identifier>",
			"name": "<Polish label>",
			"order": <1-based integer>,
			"minValue": <BigDecimal>,
			"maxValue": <BigDecimal>,
			"step":    <BigDecimal>
			}
			RADIO_BUTTONS
			{
			"type": "RADIO_BUTTONS",
			"id": "<snake_case_english_identifier>",
			"name": "<Polish label>",
			"order": <1-based integer>,
			"nameValueOptions": {
			"<visible option 1>": <BigDecimal>,
			"<visible option 2>": <BigDecimal>,
			…
			}
			}
			
			Allowed output structure
			{
			"name":      "<Polish label>",
			"formula":   "<expression using ${<inputId>} placeholders>",
			"precision": <non-negative integer>,
			"order":     <1-based integer>
			}
			
			Expression language
			Operators: +  -  *  /  ^  %
			Functions: SQRT, POW, LN, LOG10, EXP, SIN, COS, TAN,
			DECIMAL, BOOLEAN, ABS,
			ROUND, ROUND_TO_N, ROUND_UP_TO_N, ROUND_DOWN_TO_N,
			MIN, MAX
			Example: "formula":
			"ROUND_TO_N(${weight} / POW(${height} / 100, 2), 1)"
			
			Rules
			title: concise Polish noun phrase, capitalized.
			description: full sentence in Polish or null if not supplied.
			All numeric literals must be JSON numbers, not strings.
			"id" values: lower-snake-case English.
			"name" values: human-friendly Polish.
			"order" starts at 1 and is consecutive within its array.
			Respond with pure JSON: no markdown fencing, no comments, no extra keys.
			If multiple calculators fit, choose the one that best matches the request.
			If the request is ambiguous, make sensible assumptions.
			
			Return ONLY the JSON object in compressed form, without any additional text or formatting.
			""";
}
