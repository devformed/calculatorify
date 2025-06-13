package com.calculatorify.controller;

import com.calculatorify.model.DashboardCard;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Anton Gorokh
 */
public class DashboardController implements HttpHandler {

    private static final List<DashboardCard> cards = List.of(
        new DashboardCard(
            "Salary Growth Calculator",
            "Calculates your projected salary after a specified period, assuming a fixed percentage raise at regular intervals."
        ),
        new DashboardCard(
            "Loan Repayment Calculator",
            "Estimate your loan repayment schedule, including interest and principal over time."
        ),
        new DashboardCard(
            "Tax Estimator",
            "Approximate your annual tax liability based on income, deductions, and filing statusCode."
        ),
        new DashboardCard(
            "Savings Planner",
            "Plan your savings goals by calculating required deposits to reach a target amount over time."
        )
    );

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
            return;
        }
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, -1);
            return;
        }
        String sessionId = getSessionId(exchange);
        if (!SessionManager.isValid(sessionId)) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_UNAUTHORIZED, -1);
            return;
        }
        String response = toJson(cards);
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String getSessionId(HttpExchange exchange) {
        String cookies = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookies == null) return null;
        for (String cookie : cookies.split(";")) {
            String[] parts = cookie.trim().split("=", 2);
            if (parts.length == 2 && "SESSIONID".equals(parts[0])) {
                return parts[1];
            }
        }
        return null;
    }

    private void addCorsHeaders(HttpExchange exchange) {
        String origin = exchange.getRequestHeaders().getFirst("Origin");
        if (origin != null) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", origin);
            exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        } else {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        }
    }

    private String toJson(List<DashboardCard> cards) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < cards.size(); i++) {
            DashboardCard card = cards.get(i);
            sb.append('{')
              .append("\"title\":")
              .append('"').append(escapeJson(card.getTitle())).append('"').append(',')
              .append("\"description\":")
              .append('"').append(escapeJson(card.getDescription())).append('"')
              .append('}');
            if (i < cards.size() - 1) sb.append(',');
        }
        sb.append(']');
        return sb.toString();
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}