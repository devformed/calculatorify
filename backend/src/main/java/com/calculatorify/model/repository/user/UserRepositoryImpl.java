package com.calculatorify.model.repository.user;

import com.calculatorify.model.dto.user.UserDto;
import com.calculatorify.model.dto.user.UserEntry;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Anton Gorokh
 */
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private static final String SQL_PERSIST = """
			INSERT INTO users_ (username_, password_)
			VALUES(?, ?)
			RETURNING id_
			""";

	private static final String SQL_MERGE = """
			UPDATE users_
			SET username_ = ?, password_ = ?
			WHERE id_ = ?
			""";

	private static final String SQL_FIND_BY_USERNAME = """
			SELECT id_, username_, password_
			FROM users_
			WHERE username_ = ?
			""";

	private final DataSource ds;

	@Override
	public UUID persist(UserDto user) {
		try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_PERSIST)) {
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPassword());
			ResultSet rs = ps.executeQuery();
			return rs.getObject("id_", UUID.class);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void merge(UserEntry user) {
		try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_MERGE)) {
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPassword());
			ps.setString(3, user.getId().toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Optional<UserEntry> findByUsername(String username) {
		try (Connection conn = ds.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USERNAME)) {
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				return Optional.empty();
			}
			return Optional.of(UserEntry.builder()
					.id(rs.getObject("id_", UUID.class))
					.username(rs.getString("username_"))
					.password(rs.getString("password_"))
					.build());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
