package com.calculatorify.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * @author Anton Gorokh
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Json {

	private static final ObjectMapper MAPPER = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	public static String toJson(Object value) throws JsonProcessingException {
		return MAPPER.writeValueAsString(value);
	}

	@SneakyThrows
	public static String toJsonSneaky(Object value) {
		return toJson(value);
	}

	public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
		return MAPPER.readValue(json, clazz);
	}

	@SneakyThrows
	public static <T> T fromJsonSneaky(String json, Class<T> clazz) {
		return fromJson(json, clazz);
	}

	public static JsonNode readTree(String json) throws JsonProcessingException {
		return MAPPER.readTree(json);
	}

	public static <T> T toValue(JsonNode node, Class<T> clazz) throws JsonProcessingException {
		return MAPPER.treeToValue(node, clazz);
	}
}
