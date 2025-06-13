package com.calculatorify.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * @author Anton Gorokh
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Json {

	private static final ObjectMapper MAPPER = new ObjectMapper();

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

	@SneakyThrows
	public static JsonNode readTreeSneaky(String json) {
		return readTree(json);
	}
}
