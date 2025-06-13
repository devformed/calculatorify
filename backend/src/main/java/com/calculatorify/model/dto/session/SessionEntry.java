package com.calculatorify.model.dto.session;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Anton Gorokh
 */
@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
public class SessionEntry {

	@NotNull
	private UUID id;

	@NotNull
	private UUID userId;

	@NotNull
	private Instant accessedAt;
}
