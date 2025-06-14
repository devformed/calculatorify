package com.calculatorify.service.notation.token;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Anton Gorokh
 */
public final class Tokenizer {

	private static final ImmutableList<TokenType> TYPES = ImmutableList.copyOf(TokenType.values());
	private static final Pattern TYPES_PATTERN = TYPES.stream()
			.map(TokenType::getPattern)
			.map(Pattern::pattern)
			.collect(Collectors.collectingAndThen(Collectors.joining("|"), joined -> Pattern.compile("\\s*(" + joined + ")\\s*")));

	public static List<Token> tokenize(String expression) {
		Matcher matcher = TYPES_PATTERN.matcher(expression);
		List<Token> result = new ArrayList<>();

		while (matcher.find()) {
			result.add(toToken(matcher.group(1)));
		}
		return result;
	}

	private static Token toToken(String value) {
		return TYPES.stream()
				.filter(type -> type.matches(value))
				.findFirst()
				.map(type -> new Token(type, value))
				.orElseThrow(() -> new IllegalStateException("Could not resolve token type for value: " + value));
	}
}
