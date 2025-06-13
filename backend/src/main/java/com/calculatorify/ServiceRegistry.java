package com.calculatorify;

import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.calculatorify.util.Utils.nn;

/**
 * @author Anton Gorokh
 */
@NoArgsConstructor
public final class ServiceRegistry {

	private static final Map<Class<?>, Object> SERVICES = new ConcurrentHashMap<>();

	public static void register(Class<?> clazz, Object service) {
		if (SERVICES.containsKey(clazz)) {
			throw new IllegalStateException("Cannot register %s: service %s already registered".formatted(clazz, SERVICES.get(clazz).getClass()));
		}
		SERVICES.put(clazz, service);
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(Class<T> clazz) {
		return (T) nn(SERVICES.get(clazz));
	}
}
