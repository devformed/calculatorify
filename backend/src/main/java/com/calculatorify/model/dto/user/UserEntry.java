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
public class UserEntry extends UserDto {

	@NotNull
	private UUID id;

	@Builder
	public UserEntry(String username, String password, String roles, UUID id) {
		super(username, password, roles);
		this.id = id;
	}
}
