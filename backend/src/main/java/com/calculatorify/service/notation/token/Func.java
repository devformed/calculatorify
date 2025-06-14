package com.calculatorify.service.notation.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Deque;

import static com.calculatorify.service.notation.NotationResolverUtils.addNumber;
import static com.calculatorify.service.notation.NotationResolverUtils.popDouble;

/**
 * @author Anton Gorokh
 */
@Getter
@RequiredArgsConstructor
public enum Func {

	DECIMAL(1) {
		@Override
		public void process(Deque<Object> stack) {
			addNumber(stack, popDouble(stack));
		}
	},
	BOOLEAN(1) {
		@Override
		public void process(Deque<Object> stack) {
			stack.add(popDouble(stack).intValue() != 0);
		}
	},
	ABS(1) {
		@Override
		public void process(Deque<Object> stack) {
			addNumber(stack, Math.abs(popDouble(stack)));
		}
	},
	ROUND_TO_N(2) {
		@Override
		public void process(Deque<Object> stack) {
			BigDecimal bd = BigDecimal.valueOf(popDouble(stack));
			int n = popDouble(stack).intValue();
			addNumber(stack, bd.setScale(n, RoundingMode.HALF_UP));
		}
	},
	ROUND_UP_TO_N(2) {
		@Override
		public void process(Deque<Object> stack) {
			BigDecimal bd = BigDecimal.valueOf(popDouble(stack));
			int n = popDouble(stack).intValue();
			addNumber(stack, bd.setScale(n, RoundingMode.UP));
		}
	},
	ROUND_DOWN_TO_N(2) {
		@Override
		public void process(Deque<Object> stack) {
			BigDecimal bd = BigDecimal.valueOf(popDouble(stack));
			int n = popDouble(stack).intValue();
			addNumber(stack, bd.setScale(n, RoundingMode.DOWN));
		}
	},
	ROUND(1) {
		@Override
		public void process(Deque<Object> stack) {
			addNumber(stack, BigDecimal.valueOf(popDouble(stack)).setScale(0, RoundingMode.HALF_UP));
		}
	},
	MIN(2) {
		@Override
		public void process(Deque<Object> stack) {
			addNumber(stack, Math.min(popDouble(stack), popDouble(stack)));
		}
	},
	MAX(2) {
		@Override
		public void process(Deque<Object> stack) {
			addNumber(stack, Math.max(popDouble(stack), popDouble(stack)));
		}
	};

	private final int argsCount;

	public abstract void process(Deque<Object> stack);
}
