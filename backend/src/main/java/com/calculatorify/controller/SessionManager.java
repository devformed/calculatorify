package com.calculatorify.controller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Anton Gorokh
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SessionManager {

    private static final Map<String, String> SESSIONS = new ConcurrentHashMap<>();

    public static String createSession(String username) {
        String sessionId = UUID.randomUUID().toString();
        SESSIONS.put(sessionId, username);
        return sessionId;
    }

    public static boolean isValid(String sessionId) {
        return sessionId != null && SESSIONS.containsKey(sessionId);
    }

    public static void invalidate(String sessionId) {
        SESSIONS.remove(sessionId);
    }
}