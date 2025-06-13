package com.calculatorify.service;

import com.calculatorify.controller.SessionManager;
import com.calculatorify.exception.HttpHandlerException;
import com.calculatorify.model.dto.http.HttpPathContext;
import com.calculatorify.model.dto.http.HttpResponse;
import com.calculatorify.model.dto.user.UserDto;
import com.calculatorify.model.dto.user.UserEntry;
import com.calculatorify.model.repository.user.UserRepository;
import com.calculatorify.util.Json;
import com.calculatorify.util.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * @author Anton Gorokh
 */
@RequiredArgsConstructor
public class LoginService {

	private static final int BCRYPT_COST_FACTOR = 12;

	private final UserRepository userRepository;
	private final SessionManager sessionManager;

	public HttpResponse login(HttpExchange exchange, HttpPathContext context) throws IOException {
		BasicAuth auth = Json.fromJsonSneaky(HttpUtils.getRequestBody(exchange), BasicAuth.class);
		UserEntry user = userRepository.findByUsername(auth.username())
				.orElseThrow(() -> new NoSuchElementException("User not found"));

		if (!BCrypt.checkpw(auth.password(), user.getPassword())) {
			throw new HttpHandlerException(401, "Unauthorized: Invalid username or password");
		}
		String sessionId = sessionManager.createSession(user.getId());
		exchange.getResponseHeaders().add("Set-Cookie", "SESSIONID=%s; Path=/".formatted(sessionId));
		return HttpResponse.ok();
	}

	public HttpResponse register(HttpExchange exchange, HttpPathContext context) throws IOException {
		BasicAuth auth = Json.fromJsonSneaky(HttpUtils.getRequestBody(exchange), BasicAuth.class);
		if (userRepository.findByUsername(auth.username()).isPresent()) {
			throw new HttpHandlerException(400, "Username %s already exists".formatted(auth.username()));
		}
		String salt = BCrypt.gensalt(BCRYPT_COST_FACTOR);
		String hash = BCrypt.hashpw(auth.password(), salt);

		UUID userId = userRepository.persist(new UserDto(auth.username(), hash));
		String sessionId = sessionManager.createSession(userId);
		exchange.getResponseHeaders().add("Set-Cookie", "SESSIONID=%s; Path=/".formatted(sessionId));
		return HttpResponse.ok(userId);
	}

	public HttpResponse logout(HttpExchange exchange, HttpPathContext context) throws IOException {
		HttpUtils.getSessionId(exchange).ifPresent(sessionManager::invalidateSession);
		return HttpResponse.ok();
	}

	private record BasicAuth(String username, String password) { }
}
