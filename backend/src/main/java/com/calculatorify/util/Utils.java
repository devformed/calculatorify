package com.calculatorify.util;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.calculatorify.util.Collects.mapSet;

/**
 * @author Anton Gorokh
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Utils {

	public static <T> Set<T> ids(Collection<? extends Identifiable<T>> items) {
		return mapSet(items, Identifiable::getId);
	}

	// syntax sugar methods

	public static <T, R> R map(@Nullable T value, Function<T, R> mapping) {
		return value == null ? null : mapping.apply(value);
	}

	public static <T, R1, R2> R2 map(@Nullable T value, Function<T, R1> mapping1, Function<R1, R2> mapping2) {
		return value == null ? null : map(mapping1.apply(value), mapping2);
	}

	public static <T, R1, R2, R3> R3 map(@Nullable T value, Function<T, R1> mapping1, Function<R1, R2> mapping2, Function<R2, R3> mapping3) {
		return value == null ? null : map(mapping1.apply(value), mapping2, mapping3);
	}

	public static <T> T nn(T value) {
		return Objects.requireNonNull(value);
	}

	public static <T> T nn(T value, T orElse) {
		return Objects.requireNonNullElse(value, orElse);
	}

	public static <T> T nn(T value, Supplier<? extends T> orElseGet) {
		return Objects.requireNonNullElseGet(value, orElseGet);
	}
}

