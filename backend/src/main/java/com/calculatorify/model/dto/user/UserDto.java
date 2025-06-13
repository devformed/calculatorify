package com.calculatorify.model.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Anton Gorokh
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDto {

	@NotNull
	private String username;

	@NotNull
	private String password;
}
