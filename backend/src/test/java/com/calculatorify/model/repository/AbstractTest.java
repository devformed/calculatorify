package com.calculatorify.model.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.UUID;
import java.util.concurrent.Callable;

import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractTest {

	private static final PostgreSQLContainer<?> CONTAINER_POSTGRES = new PostgreSQLContainer<>("postgres:17.5")
			.withCreateContainerCmdModifier(cmd -> cmd.withName("calculatorify-test-postgres"))
			.withDatabaseName("testdb")
			.withUsername("test")
			.withPassword("test")
			.withReuse(true);

	protected static final UUID TEST_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

   protected static HikariDataSource dataSource;
   protected static MockedStatic<TransactionContext> txSpy;

   // Initialize container, database schema, and transaction context once per test class
   static {
       CONTAINER_POSTGRES.start();
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
       txSpy.when(() -> TransactionContext.inTransaction(any(Callable.class)))
            .thenAnswer(args -> args.getArgument(0, Callable.class).call());
   }


	@BeforeEach
	public void beforeEach() {
		TransactionContext.begin();
	}

	@AfterEach
	public void afterEach() {
		if (!TransactionContext.isTransactionActive()) {
			return;
		}
		TransactionContext.rollback();
		TransactionContext.end();
	}
}