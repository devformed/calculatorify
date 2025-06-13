package com.calculatorify.service;

import com.calculatorify.model.dto.calculator.CalculatorEntry;
import com.calculatorify.model.dto.http.HttpPathContext;
import com.calculatorify.model.dto.http.HttpResponse;
import com.calculatorify.util.Json;
import com.calculatorify.util.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author Anton Gorokh
 */
public class CalculatorService {

	private static final List<CalculatorEntry> CALCULATORS = List.of(
			new CalculatorEntry(
					UUID.randomUUID(),
					"Salary Growth Calculator",
					"Calculates your projected salary after a specified period, assuming a fixed percentage raise at regular intervals.",
					null
			),
			new CalculatorEntry(
					UUID.randomUUID(),
					"Loan Repayment Calculator",
					"Estimate your loan repayment schedule, including interest and principal over time.",
					null
			),
			new CalculatorEntry(
					UUID.randomUUID(),
					"Tax Estimator",
					"Approximate your annual tax liability based on income, deductions, and filing statusCode.",
					null
			),
			new CalculatorEntry(
					UUID.randomUUID(),
					"Savings Planner",
					"Plan your savings goals by calculating required deposits to reach a target amount over time.",
					null
			)
	);

	public HttpResponse getCalculators(HttpExchange exchange, HttpPathContext context) throws IOException {
		String fullText = HttpUtils.getRequestBody(exchange);
		return HttpResponse.ok(Json.toJson(CALCULATORS));
	}
}
