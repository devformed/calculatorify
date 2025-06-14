package com.calculatorify.model.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Anton Gorokh
 */
@Getter
@Setter
@AllArgsConstructor
public class UserDto {

	@NotNull
	private String username;

	@NotNull
	private String password;

	@NotNull
	private Set<Role> roles;

	public UserDto(String username, String password, String roles) {
		this.username = username;
		this.password = password;
		setRolesString(roles);
	}

	public String getRolesString() {
		return roles.stream()
				.map(Enum::name)
				.reduce((a, b) -> a + "," + b)
				.map(",%s,"::formatted)
				.orElse("");
	}

	public void setRolesString(String roles) {
		if (roles == null || roles.isBlank()) {
			this.roles = Set.of();
			return;
		}
		this.roles = Set.of(roles.split(","))
				.stream()
				.map(String::trim)
				.filter(role -> !role.isEmpty())
				.map(Role::valueOf)
				.collect(Collectors.toSet());
	}
}
