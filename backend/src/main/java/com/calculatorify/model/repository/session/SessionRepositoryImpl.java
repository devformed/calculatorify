package com.calculatorify.model.repository.session;

import com.calculatorify.model.dto.session.SessionDto;
import com.calculatorify.model.dto.session.SessionEntry;
import com.calculatorify.model.repository.TransactionContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class SessionRepositoryImpl implements SessionRepository {

    private static final String SQL_PERSIST = """
        INSERT INTO session_ (user_id_, accessed_at_)
        VALUES (?, ?)
        RETURNING id_
        """;

    private static final String SQL_MERGE = """
        UPDATE session_
        SET user_id_ = ?, accessed_at_ = ?
        WHERE id_ = ?
        """;

    private static final String SQL_FIND_BY_ID = """
        SELECT id_, user_id_, accessed_at_
        FROM session_
        WHERE id_ = ?
        """;

    private static final String SQL_DELETE = """
        DELETE FROM session_
        WHERE id_ = ?
        """;
    private static final String SQL_DELETE_OLDER_THAN = """
        DELETE FROM session_
        WHERE accessed_at_ < ?
        """;

    @Override
    public UUID persist(SessionDto session) {
        Connection conn = TransactionContext.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SQL_PERSIST)) {
            ps.setObject(1, session.getUserId(), Types.OTHER);
            ps.setTimestamp(2, Timestamp.from(session.getAccessedAt()));
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
    public void merge(SessionEntry session) {
        Connection conn = TransactionContext.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SQL_MERGE)) {
            ps.setObject(1, session.getUserId(), Types.OTHER);
            ps.setTimestamp(2, Timestamp.from(session.getAccessedAt()));
            ps.setObject(3, session.getId(), Types.OTHER);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SessionEntry> findById(UUID id) {
        Connection conn = TransactionContext.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setObject(1, id, Types.OTHER);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return Optional.empty();
            }
            return Optional.of(SessionEntry.builder()
                .id(rs.getObject("id_", UUID.class))
                .userId(rs.getObject("user_id_", UUID.class))
                .accessedAt(rs.getTimestamp("accessed_at_").toInstant())
                .build());
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

    @Override
    public void deleteByAccessedAtOlderThan(Instant time) {
        Connection conn = TransactionContext.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE_OLDER_THAN)) {
            ps.setTimestamp(1, Timestamp.from(time));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}