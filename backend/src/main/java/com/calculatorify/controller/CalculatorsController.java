package com.calculatorify.controller;

import com.calculatorify.model.dto.http.HttpMethod;
import com.calculatorify.service.CalculatorService;
import com.calculatorify.service.HttpContextMatcher;
import com.calculatorify.service.HttpRequestHandler;
import com.google.common.collect.ImmutableMap;

/**
 * @author Anton Gorokh
 */
public class CalculatorsController extends AbstractController {

    public CalculatorsController(SessionManager sessionManager, CalculatorService service) {
        super(sessionManager, buildHandlers(service));
    }

    private static ImmutableMap<HttpContextMatcher, HttpRequestHandler> buildHandlers(CalculatorService service) {
        return ImmutableMap.<HttpContextMatcher, HttpRequestHandler>builder()
                .put(HttpContextMatcher.of(HttpMethod.POST, "/calculators/construct"), service::construct)
                .put(HttpContextMatcher.of(HttpMethod.GET, "/calculators"), service::getCalculators)
                .put(HttpContextMatcher.of(HttpMethod.GET, "/calculators/{id}"), service::getCalculator)
                .put(HttpContextMatcher.of(HttpMethod.PUT, "/calculators/{id}"), service::updateCalculator)
                .put(HttpContextMatcher.of(HttpMethod.DELETE, "/calculators/{id}"), service::deleteCalculator)
                .build();
    }
}