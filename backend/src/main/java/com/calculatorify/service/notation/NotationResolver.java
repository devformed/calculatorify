package com.calculatorify.service.notation;

import com.calculatorify.service.notation.token.Func;
import com.calculatorify.service.notation.token.Token;
import com.calculatorify.service.notation.token.TokenType;
import com.calculatorify.service.notation.token.Tokenizer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import static com.calculatorify.service.notation.NotationResolverUtils.addNumber;
import static com.calculatorify.service.notation.NotationResolverUtils.popDouble;

/**
 * @author Anton Gorokh
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotationResolver {

	public static Object resolve(String expression, Map<String, String> variableByIdMap) {
		Deque<Object> stack = new ArrayDeque<>();
		List<Token> postfix = ShuntingYardConverter.toPostfix(Tokenizer.tokenize(expression));
		replaceVariables(postfix, variableByIdMap);

		postfix.forEach(token -> processToken(stack, token));
		return stack.remove();
	}

	private static void replaceVariables(List<Token> postfix, Map<String, String> variableByIdMap) {
		for (int i = 0; i < postfix.size(); i++) {
			if (TokenType.VARIABLE != postfix.get(i).type()) {
				continue;
			}
			Token variableToken = postfix.get(i);
			String newValue = variableByIdMap.get(variableToken.value());
			if (newValue == null) {
				throw new IllegalArgumentException("Unknown variable: " + variableToken.value());
			}
			TokenType newType = NumberUtils.isParsable(newValue)
					? TokenType.DECIMAL_LITERAL
					: BooleanUtils.toBooleanObject(newValue) != null
					? TokenType.BOOLEAN_LITERAL
					: TokenType.STRING_LITERAL;
			postfix.set(i, new Token(newType, newValue));
		}
	}

	private static void processToken(Deque<Object> stack, Token token) {
		switch (token.type()) {
			case DECIMAL_LITERAL -> addNumber(stack, Double.parseDouble(token.value()));
			case BOOLEAN_LITERAL -> stack.add(BooleanUtils.toBoolean(token.value()));
			case STRING_LITERAL -> stack.add(token.value());
			case OPERATOR -> handleOperator(stack, token);
			case FUNCTION -> handleFunction(stack, token);
			default -> throw new IllegalStateException("Unsupported token %s in postfix processing".formatted(token.type()));
		}
	}

	private static void handleOperator(Deque<Object> stack, Token token) {
		var left = popDouble(stack);
		var right = popDouble(stack);

		switch (token.value()) {
			case "+" -> addNumber(stack, left + right);
			case "-" -> addNumber(stack, left - right);
			case "*" -> addNumber(stack, left * right);
			case "/" -> addNumber(stack, left / right);
			case "^" -> addNumber(stack, Math.pow(left, right));
			default -> throw new UnsupportedOperationException("Unsupported operator: " + token.value());
		}
	}

	private static void handleFunction(Deque<Object> stack, Token token) {
		Func.valueOf(token.value()).process(stack);
	}
}
