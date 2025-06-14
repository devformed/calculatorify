package com.calculatorify.util.http;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Anton Gorokh
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpConstants {

	public static final String COOKIE_SESSION_ID = "SESSIONID";

	public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
	public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
}
