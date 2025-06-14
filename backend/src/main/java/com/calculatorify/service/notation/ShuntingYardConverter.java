package com.calculatorify.service.notation;


import com.calculatorify.service.notation.token.Func;
import com.calculatorify.service.notation.token.Token;
import com.calculatorify.service.notation.token.TokenType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static com.calculatorify.util.Collects.mapList;

/**
 * @author Anton Gorokh
 */
public final class ShuntingYardConverter {

	public static List<Token> toPostfix(List<Token> infix) {
		List<Token> output = new ArrayList<>();
		Deque<Token> stack = new ArrayDeque<>();

		infix.forEach(token -> handleToken(token, stack, output));

		while (!stack.isEmpty()) {
			Token token = stack.pop();
			if (token.type() == TokenType.LEFT_PAREN || token.type() == TokenType.RIGHT_PAREN) {
				throw new IllegalExpressionException(NotationErrors.PARENTHESES_DONT_MATCH);
			}
			output.add(token);
		}

		validateValence(output);
		return output;
	}

	private static void validateValence(List<Token> output) {
		int valence = 0;
		for (Token token : output) {
			switch (token.type()) {
				case OPERATOR -> valence -= 1;
				case FUNCTION -> valence += 1 - Func.valueOf(token.value()).getArgsCount();
				default -> valence += 1;
			}
			if (valence <= 0) {
				System.out.println(token.value() + " " + mapList(output, Token::value));
				throw new IllegalExpressionException(NotationErrors.VALENCE_REACHED_ZERO);
			}
		}
		if (valence != 1) {
			throw new IllegalExpressionException(NotationErrors.VALENCE_MORE_THAN_ONE);
		}
	}

	private static void handleToken(Token token, Deque<Token> stack, List<Token> output) {
		switch (token.type()) {
			case DECIMAL_LITERAL, BOOLEAN_LITERAL, STRING_LITERAL, VARIABLE -> output.add(token);
			case FUNCTION, LEFT_PAREN -> stack.push(token);
			case COMMA -> handleComma(stack, output);
			case OPERATOR -> handleOperator(token, stack, output);
			case RIGHT_PAREN -> handleRightParen(stack, output);
			default -> throw new IllegalStateException("Token %s handling not implemented".formatted(token));
		}
	}

	private static void handleComma(Deque<Token> stack, List<Token> output) {
		while (!stack.isEmpty() && stack.peek().type() != TokenType.LEFT_PAREN) {
			output.add(stack.pop());
		}
		if (stack.isEmpty()) {
			throw new IllegalExpressionException(NotationErrors.COMMA_OUTSIDE_OF_FUNCTION_CONTEXT);
		}
	}

	private static void handleOperator(Token operator, Deque<Token> stack, List<Token> output) {
		while (!stack.isEmpty() && TokenType.LEFT_PAREN != stack.peek().type() && getPrecedence(stack.peek()) >= getPrecedence(operator)) {
			output.add(stack.pop());
		}
		stack.push(operator);
	}

	private static void handleRightParen(Deque<Token> stack, List<Token> output) {
		while (!stack.isEmpty() && TokenType.LEFT_PAREN != stack.peek().type()) {
			output.add(stack.pop());
		}
		if (stack.isEmpty()) {
			throw new IllegalExpressionException(NotationErrors.PARENTHESES_DONT_MATCH);
		}
		stack.pop();

		if (!stack.isEmpty() && TokenType.FUNCTION == stack.peek().type()) {
			output.add(stack.pop());
		}
	}

	private static int getPrecedence(Token token) {
		return switch (token.type()) {
			case OPERATOR -> {
				String value = token.value();
				if (value.charAt(0) == '+' || value.charAt(0) == '-') {
					yield 1;
				}
				yield 2;
			}
			case FUNCTION -> 3;
			default -> 0;
		};
	}
}
