package com.calculatorify.model.repository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TransactionContext {

	private static final ThreadLocal<Connection> CONTEXT = new ThreadLocal<>();

	private static DataSource ds;

	public static void setDataSource(DataSource dataSource) {
		if (ds != null) {
			throw new IllegalStateException("DataSource already set");
		}
		ds = dataSource;
	}

	public static <T> T inTransaction(Callable<T> callable) throws Exception {
		if (CONTEXT.get() != null) {
			throw new IllegalStateException("Transaction already started on this thread");
		}
		try {
			begin();
			var value = callable.call();
			commit();
			return value;
		} catch (Exception e) {
			rollback();
			throw e;
		} finally {
			end();
		}
	}

	public static Connection getConnection() {
		Connection txConn = CONTEXT.get();
		if (txConn != null) {
			return txConn;
		}
		try {
			return ds.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("Failed to get connection", e);
		}
	}

	public static void begin() {
		try {
			Connection conn = ds.getConnection();
			conn.setAutoCommit(false);
			CONTEXT.set(conn);
		} catch (SQLException e) {
			throw new RuntimeException("Failed to begin transaction", e);
		}
	}

	public static void commit() {
		Connection conn = CONTEXT.get();
		if (conn == null) {
			return;
		}
		try {
			conn.commit();
		} catch (SQLException e) {
			throw new RuntimeException("Failed to commit transaction", e);
		}
	}

	public static void rollback() {
		Connection conn = CONTEXT.get();
		if (conn == null) {
			return;
		}
		try {
			conn.rollback();
		} catch (SQLException e) {
			throw new RuntimeException("Failed to rollback transaction", e);
		}
	}

	public static void end() {
		Connection conn = CONTEXT.get();
		CONTEXT.remove();
		if (conn == null) {
			return;
		}
		try {
			conn.setAutoCommit(true);
		} catch (SQLException ignore) {
		}
		try {
			conn.close();
		} catch (SQLException ignore) {
		}
	}

    public static boolean isTransactionActive() {
        return CONTEXT.get() != null;
    }
}