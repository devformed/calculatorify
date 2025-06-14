package com.calculatorify.model.repository.history;

import com.calculatorify.model.dto.history.HistoryDto;
import com.calculatorify.model.dto.history.HistoryEntry;
import com.calculatorify.model.repository.TransactionContext;

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

public class HistoryRepositoryImpl implements HistoryRepository {

	private static final String SQL_PERSIST = """
			INSERT INTO history_ (user_id_, calculator_id_, accessed_at_)
			VALUES (?, ?, ?)
			RETURNING id_
			""";

	private static final String SQL_MERGE = """
			UPDATE history_
			SET user_id_ = ?, calculator_id_ = ?, accessed_at_ = ?
			WHERE id_ = ?
			""";

	private static final String SQL_FIND_BY_USER_ID_AND_CALC_ID = """
			SELECT id_, user_id_, calculator_id_, accessed_at_
			FROM history_
			WHERE user_id_ = ?
			AND calculator_id_ = ?
			""";

	private static final String SQL_FIND_BY_USER_ID = """
			SELECT id_, user_id_, calculator_id_, accessed_at_
			FROM history_
			WHERE user_id_ = ?
			ORDER BY accessed_at_ DESC
			""";

	@Override
	public UUID persist(HistoryDto history) {
		Connection conn = TransactionContext.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(SQL_PERSIST)) {
			ps.setObject(1, history.getUserId(), Types.OTHER);
			ps.setObject(2, history.getCalculatorId(), Types.OTHER);
			ps.setTimestamp(3, Timestamp.from(history.getAccessedAt()));
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
	public void merge(HistoryEntry history) {
		Connection conn = TransactionContext.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(SQL_MERGE)) {
			ps.setObject(1, history.getUserId(), Types.OTHER);
			ps.setObject(2, history.getCalculatorId(), Types.OTHER);
			ps.setTimestamp(3, Timestamp.from(history.getAccessedAt()));
			ps.setObject(4, history.getId(), Types.OTHER);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Optional<HistoryEntry> findByUserIdAndCalculatorId(UUID userId, UUID calculatorId) {
		Connection conn = TransactionContext.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_ID_AND_CALC_ID)) {
			ps.setObject(1, userId, Types.OTHER);
			ps.setObject(2, calculatorId, Types.OTHER);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				return Optional.empty();
			}
			return Optional.of(HistoryEntry.builder()
					.id(rs.getObject("id_", UUID.class))
					.userId(rs.getObject("user_id_", UUID.class))
					.calculatorId(rs.getObject("calculator_id_", UUID.class))
					.accessedAt(rs.getTimestamp("accessed_at_").toInstant())
					.build());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<HistoryEntry> findByUserId(UUID userId) {
		Connection conn = TransactionContext.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_ID)) {
			ps.setObject(1, userId, Types.OTHER);
			ResultSet rs = ps.executeQuery();
			var list = new ArrayList<HistoryEntry>();
			while (rs.next()) {
				list.add(HistoryEntry.builder()
						.id(rs.getObject("id_", UUID.class))
						.userId(rs.getObject("user_id_", UUID.class))
						.calculatorId(rs.getObject("calculator_id_", UUID.class))
						.accessedAt(rs.getTimestamp("accessed_at_").toInstant())
						.build());
			}
			return list;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}