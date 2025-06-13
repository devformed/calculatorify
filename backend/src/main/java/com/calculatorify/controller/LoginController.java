package com.calculatorify.controller;

import com.calculatorify.model.dto.http.HttpMethod;
import com.calculatorify.service.HttpContextMatcher;
import com.calculatorify.service.HttpRequestHandler;
import com.calculatorify.service.LoginService;
import com.google.common.collect.ImmutableMap;

/**
 * @author Anton Gorokh
 */
public class LoginController extends AbstractController {

	public LoginController(SessionManager sessionManager, LoginService service) {
		super(sessionManager, buildHandlers(service));
	}

	private static ImmutableMap<HttpContextMatcher, HttpRequestHandler> buildHandlers(LoginService service) {
		// Map POST /login, /register, and /logout to respective handlers
		return ImmutableMap.<HttpContextMatcher, HttpRequestHandler>builder()
			.put(HttpContextMatcher.of(HttpMethod.POST, "/login"), service::login)
			.put(HttpContextMatcher.of(HttpMethod.POST, "/register"), service::register)
			.put(HttpContextMatcher.of(HttpMethod.POST, "/logout"), service::logout)
			.build();
	}
}