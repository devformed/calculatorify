package com.calculatorify.model.repository.calculator;

import com.calculatorify.model.dto.calculator.CalculatorDto;
import com.calculatorify.model.dto.calculator.CalculatorEntry;
import com.calculatorify.model.dto.calculator.config.CalculatorConfig;
import com.calculatorify.model.repository.TransactionContext;
import com.calculatorify.util.Json;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CalculatorRepositoryImpl implements CalculatorRepository {

	private static final String SQL_PERSIST = """
			INSERT INTO calculator_ (title_, description_, config_, created_at_, updated_at_, user_id_)
			VALUES (?, ?, ?::jsonb, ?, ?, ?)
			RETURNING id_
			""";

	private static final String SQL_MERGE = """
			UPDATE calculator_
			SET title_ = ?, description_ = ?, config_ = ?::jsonb, updated_at_ = ?, user_id_ = ?
			WHERE id_ = ?
			""";

	private static final String SQL_FIND_BY_ID = """
			SELECT id_, title_, description_, config_, created_at_, updated_at_, user_id_
			FROM calculator_
			WHERE id_ = ?
			""";
	private static final String SQL_FIND_ALL_BY_FULL_TEXT = """
			SELECT id_, title_, description_, config_, created_at_, updated_at_, user_id_
			FROM calculator_
			WHERE (? IS NULL OR title_ ILIKE ? OR description_ ILIKE ?)
			ORDER BY created_at_ DESC
			""";

	private static final String SQL_DELETE = """
			DELETE FROM calculator_
			WHERE id_ = ?
			""";

	@Override
	public UUID persist(CalculatorDto calculator) {
		Connection conn = TransactionContext.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(SQL_PERSIST)) {
			ps.setString(1, calculator.getTitle());
			if (calculator.getDescription() != null) {
				ps.setString(2, calculator.getDescription());
			} else {
				ps.setNull(2, Types.VARCHAR);
			}
			ps.setString(3, Json.toJsonSneaky(calculator.getConfig()));
			ps.setTimestamp(4, Timestamp.from(calculator.getCreatedAt()));
			ps.setTimestamp(5, Timestamp.from(calculator.getUpdatedAt()));
			ps.setObject(6, calculator.getUserId(), Types.OTHER);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				throw new SQLException("INSERT did not return an ID");
			}
			return rs.getObject("id_", UUID.class);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void merge(CalculatorEntry calculator) {
		Connection conn = TransactionContext.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(SQL_MERGE)) {
			ps.setString(1, calculator.getTitle());
			ps.setString(2, calculator.getDescription());
			ps.setString(3, Json.toJsonSneaky(calculator.getConfig()));
			ps.setTimestamp(4, Timestamp.from(calculator.getUpdatedAt()));
			ps.setObject(5, calculator.getUserId(), Types.OTHER);
			ps.setObject(6, calculator.getId(), Types.OTHER);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Optional<CalculatorEntry> findById(UUID id) {
		Connection conn = TransactionContext.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
			ps.setObject(1, id, Types.OTHER);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				return Optional.empty();
			}
			String configJson = rs.getString("config_");
			CalculatorConfig config = Json.fromJsonSneaky(configJson, CalculatorConfig.class);
			return Optional.of(CalculatorEntry.builder()
					.id(rs.getObject("id_", UUID.class))
					.title(rs.getString("title_"))
					.description(rs.getString("description_"))
					.config(config)
					.createdAt(rs.getTimestamp("created_at_").toInstant())
					.updatedAt(rs.getTimestamp("updated_at_").toInstant())
					.userId(rs.getObject("user_id_", UUID.class))
					.build());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<CalculatorEntry> findAllByFullText(String text) {
		Connection conn = TransactionContext.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_FULL_TEXT)) {
			ps.setString(1, text);
			String pattern = text == null ? null : "%" + text + "%";
			ps.setString(2, pattern);
			ps.setString(3, pattern);
			ResultSet rs = ps.executeQuery();
			var list = new ArrayList<CalculatorEntry>();
			while (rs.next()) {
				CalculatorEntry entry = CalculatorEntry.builder()
						.id(rs.getObject("id_", UUID.class))
						.title(rs.getString("title_"))
						.description(rs.getString("description_"))
						.config(Json.fromJsonSneaky(rs.getString("config_"), CalculatorConfig.class))
						.createdAt(rs.getTimestamp("created_at_").toInstant())
						.updatedAt(rs.getTimestamp("updated_at_").toInstant())
						.userId(rs.getObject("user_id_", UUID.class))
						.build();
				list.add(entry);
			}
			return list;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete(UUID id) {
		Connection conn = TransactionContext.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
			ps.setObject(1, id, Types.OTHER);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}