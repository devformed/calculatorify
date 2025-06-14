package com.calculatorify.controller;

import com.calculatorify.model.dto.http.HttpMethod;
import com.calculatorify.service.HistoryService;
import com.calculatorify.service.HttpContextMatcher;
import com.calculatorify.service.HttpRequestHandler;
import com.google.common.collect.ImmutableMap;

/**
 * @author Anton Gorokh
 */
public class HistoryController extends AbstractController {

	public HistoryController(SessionManager sessionManager, HistoryService service) {
		super(sessionManager, buildHandlers(service));
	}

	private static ImmutableMap<HttpContextMatcher, HttpRequestHandler> buildHandlers(HistoryService service) {
		return ImmutableMap.<HttpContextMatcher, HttpRequestHandler>builder()
				.put(HttpContextMatcher.of(HttpMethod.GET, "/history"), service::findAll)
				.build();
	}
}
