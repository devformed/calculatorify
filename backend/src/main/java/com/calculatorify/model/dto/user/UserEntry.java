package com.calculatorify.model.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author Anton Gorokh
 */
@Getter
@Setter
@Builder
public class UserEntry {
	@NotNull
	private UUID id;
	@NotNull
	private String username;
	@NotNull
	private String password;
}
