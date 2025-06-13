package com.calculatorify.service;

import com.calculatorify.model.dto.http.HttpMethod;
import com.calculatorify.model.dto.http.HttpPathContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTTP path matcher and parser
 * @author Anton Gorokh
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpContextMatcher {

	private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\{([a-zA-Z0-9_]+)}");

	private final HttpMethod httpMethod;
	private final List<String> pathVariables;
	private final Pattern pathPattern;

	/**
	 * Creates a new {@link HttpContextMatcher} for the given path template.
	 * The template can contain path variables in the form of "{variableName}".
	 * Example: "/api/users/{userId}/posts/{postId}"
	 * @param template the path template
	 * @return a new instance of {@link HttpContextMatcher}
	 */
	public static HttpContextMatcher of(HttpMethod method, String template) {
		List<String> variables = new ArrayList<>();
		Matcher m0 = PATH_VARIABLE_PATTERN.matcher(template);
		int last = 0;
		StringBuilder regex = new StringBuilder();
		while (m0.find()) {
			// escape literal text before the variable
			regex.append(Pattern.quote(template.substring(last, m0.start())));
			String name = m0.group(1);
			variables.add(name);
			// named group matches any chars except '/'
			regex.append("(?<").append(name).append(">[^/]+)");
			last = m0.end();
		}
		// append any trailing literal text
		regex.append(Pattern.quote(template.substring(last)));
		// match the entire path
		Pattern parameters = Pattern.compile("^" + regex + "$");
		return new HttpContextMatcher(method, variables, parameters);
	}

	/**
	 * Checks whether the path (ignoring any "?…" query params) matches the template.
	 * @return true if the path matches the template, false otherwise
	 */
	public boolean match(HttpMethod method, URI path) {
		if (method != httpMethod) return false;
		String raw = path.getRawPath();
		int queryIndex = raw.indexOf('?');
		if (queryIndex >= 0) raw = raw.substring(0, queryIndex);
		return pathPattern.matcher(raw).matches();
	}

	/**
	 * Parses the path and extracts path variables and query parameters.
	 * @param path the full HTTP path, including query string if present
	 * @return an instance of {@link HttpPathContext} containing path variables and query parameters
	 * @throws IllegalStateException if the path does not match the template
	 */
	public HttpPathContext parse(URI path) {
		String fullPath = path.getRawPath();
		String rawPath = fullPath;
		String qs = null;
		int idx = fullPath.indexOf('?');
		if (idx >= 0) {
			rawPath = fullPath.substring(0, idx);
			qs = fullPath.substring(idx + 1);
		}

		Matcher m = pathPattern.matcher(rawPath);
		if (!m.matches()) {
			throw new IllegalStateException("Path '" + rawPath + "' does not match template");
		}

		Map<String, String> variables = new LinkedHashMap<>();
		for (String name : pathVariables) {
			variables.put(name, m.group(name));
		}

		Map<String, String> parameters = new LinkedHashMap<>();
		if (qs != null && !qs.isEmpty()) {
			for (String pair : qs.split("&")) {
				String[] parts = pair.split("=", 2);
				String key = decode(parts[0]);
				String value = parts.length > 1 ? decode(parts[1]) : "";
				parameters.put(key, value);
			}
		}
		return new HttpPathContext(variables, parameters);
	}

	private static String decode(String s) {
		return URLDecoder.decode(s, StandardCharsets.UTF_8);
	}
}
