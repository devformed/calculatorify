package com.calculatorify.util;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Anton Gorokh
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Collects {

	// syntax sugar methods

	public static <T, R> List<R> mapList(@Nullable Collection<T> items, Function<T, R> mapper) {
		return mapStream(items, mapper).toList();
	}

	public static <T, R> Set<R> mapSet(Collection<T> items, Function<T, R> mapper) {
		return mapStream(items, mapper).collect(Collectors.toSet());
	}

	private static <T, R> Stream<R> mapStream(@Nullable Collection<T> items, Function<T, R> mapper) {
		return items == null ? Stream.empty() : items.stream().map(mapper);
	}
}
