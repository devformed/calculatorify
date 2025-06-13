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

	public LoginController(LoginService service) {
		super(buildHandlers(service));
	}

	private static ImmutableMap<HttpContextMatcher, HttpRequestHandler> buildHandlers(LoginService service) {
		return ImmutableMap.of(
				HttpContextMatcher.of(HttpMethod.POST, "/login"), service::login,
				HttpContextMatcher.of(HttpMethod.POST, "/register"), service::register
		);
	}
}