package com.calculatorify;

import com.calculatorify.controller.CalculatorsController;
import com.calculatorify.controller.HistoryController;
import com.calculatorify.controller.LoginController;
import com.calculatorify.controller.SessionManager;
import com.calculatorify.model.repository.DataSourceProvider;
import com.calculatorify.model.repository.DataSourceProviderImpl;
import com.calculatorify.model.repository.TransactionContext;
import com.calculatorify.model.repository.calculator.CalculatorRepository;
import com.calculatorify.model.repository.calculator.CalculatorRepositoryImpl;
import com.calculatorify.model.repository.history.HistoryRepository;
import com.calculatorify.model.repository.history.HistoryRepositoryImpl;
import com.calculatorify.model.repository.session.SessionRepository;
import com.calculatorify.model.repository.session.SessionRepositoryImpl;
import com.calculatorify.model.repository.user.UserRepository;
import com.calculatorify.model.repository.user.UserRepositoryImpl;
import com.calculatorify.service.CalculatorService;
import com.calculatorify.service.HistoryService;
import com.calculatorify.service.LoginService;
import com.sun.net.httpserver.HttpServer;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.InetSocketAddress;

import static com.calculatorify.ServiceRegistry.get;

public class Application {

	private static final int PORT = 8080;

	public static void main(String[] args) throws Exception {
		registerServices();
		migrateDatabase();
		startHttpServer();
	}

	private static void startHttpServer() throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
		server.createContext("/login", get(LoginController.class));
		server.createContext("/register", get(LoginController.class));
		server.createContext("/logout", get(LoginController.class));
		server.createContext("/calculators", get(CalculatorsController.class));
		server.createContext("/history", get(HistoryController.class));
		server.setExecutor(null);
		server.start();
		System.out.println("Server started on port " + PORT);
	}

	private static void registerServices() {
		// repository
		ServiceRegistry.register(DataSourceProvider.class, new DataSourceProviderImpl());
		TransactionContext.setDataSource(get(DataSourceProvider.class).getDataSource());

		ServiceRegistry.register(UserRepository.class, new UserRepositoryImpl());
		ServiceRegistry.register(SessionRepository.class, new SessionRepositoryImpl());
		ServiceRegistry.register(CalculatorRepository.class, new CalculatorRepositoryImpl());
		ServiceRegistry.register(HistoryRepository.class, new HistoryRepositoryImpl());
		// service
		ServiceRegistry.register(SessionManager.class, new SessionManager(get(SessionRepository.class), get(UserRepository.class)));
		ServiceRegistry.register(LoginService.class, new LoginService(get(UserRepository.class), get(SessionManager.class)));
		ServiceRegistry.register(HistoryService.class, new HistoryService(get(HistoryRepository.class), get(SessionManager.class)));
		ServiceRegistry.register(CalculatorService.class, new CalculatorService(get(CalculatorRepository.class), get(SessionManager.class), get(HistoryService.class)));
		// controllers
		ServiceRegistry.register(LoginController.class, new LoginController(get(SessionManager.class), get(LoginService.class)));
		ServiceRegistry.register(CalculatorsController.class, new CalculatorsController(get(SessionManager.class), get(CalculatorService.class)));
		ServiceRegistry.register(HistoryController.class, new HistoryController(get(SessionManager.class), get(HistoryService.class)));
	}

	private static void migrateDatabase() {
		DataSource ds = get(DataSourceProvider.class).getDataSource();
		Flyway.configure()
				.dataSource(ds)
				.locations("classpath:db/migration")
				.validateMigrationNaming(true)
				.placeholderReplacement(false)
				.load()
				.migrate();
	}
}