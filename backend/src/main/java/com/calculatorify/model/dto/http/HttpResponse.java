package com.calculatorify.model.dto.http;

import com.calculatorify.util.http.HttpConstants;

/**
 * @author Anton Gorokh
 */
public record HttpResponse(int statusCode, Object content, String contentType) {

	private static final HttpResponse OK = new HttpResponse(200, null, HttpConstants.CONTENT_TYPE_APPLICATION_JSON);

	public static <T> HttpResponse ok() {
		return OK;
	}

	public static <T> HttpResponse ok(T response) {
		return new HttpResponse(200, response, HttpConstants.CONTENT_TYPE_APPLICATION_JSON);
	}

	public static <T> HttpResponse text(int status, T response) {
		return new HttpResponse(status, response, HttpConstants.CONTENT_TYPE_TEXT_PLAIN);
	}
}
