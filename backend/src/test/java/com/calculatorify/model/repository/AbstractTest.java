package com.calculatorify.model.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.concurrent.Callable;

import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractTest {

	private static final PostgreSQLContainer<?> CONTAINER_POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine")
			.withCreateContainerCmdModifier(cmd -> cmd.withName("calculatorify-test-postgres"))
			.withDatabaseName("testdb")
			.withUsername("test")
			.withPassword("test")
			.withReuse(true);

	static {
		CONTAINER_POSTGRES.start();
	}

	protected HikariDataSource dataSource;
	protected MockedStatic<TransactionContext> txSpy;

	@BeforeAll
	public void beforeAll() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(CONTAINER_POSTGRES.getJdbcUrl());
		config.setUsername(CONTAINER_POSTGRES.getUsername());
		config.setPassword(CONTAINER_POSTGRES.getPassword());
		config.setDriverClassName("org.postgresql.Driver");

		try (HikariDataSource migrationDs = new HikariDataSource(config)) {
			Flyway.configure()
					.dataSource(migrationDs)
					.locations("classpath:db/migration")
					.load()
					.migrate();
		}
		dataSource = new HikariDataSource(config);
		TransactionContext.setDataSource(dataSource);
		txSpy = mockStatic(TransactionContext.class, CALLS_REAL_METHODS);

		// wrap the whole test with a transaction that will be rolled back after each test
		txSpy.when(() -> TransactionContext.inTransaction(any(Callable.class)))
				.thenAnswer(args -> {
					return args.getArgument(0, Callable.class).call();
				});
	}

	@BeforeEach
	public void beforeEach() {
		TransactionContext.begin();
	}

	@AfterEach
	public void afterEach() {
		TransactionContext.rollback();
		TransactionContext.end();
	}

	@AfterAll
	public void afterAll() {
		if (dataSource != null) {
			dataSource.close();
		}
		if (CONTAINER_POSTGRES != null) {
			CONTAINER_POSTGRES.stop();
		}
	}
}