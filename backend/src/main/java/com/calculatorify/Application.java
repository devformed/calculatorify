package com.calculatorify;

import com.calculatorify.controller.DashboardController;
import com.calculatorify.controller.LoginController;
import com.calculatorify.model.repository.DataSourceProvider;
import com.calculatorify.model.repository.DataSourceProviderImpl;
import com.calculatorify.model.repository.TransactionContext;
import com.calculatorify.model.repository.user.UserRepository;
import com.calculatorify.model.repository.user.UserRepositoryImpl;
import com.calculatorify.service.LoginService;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;

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
        server.createContext("/api/login/*", get(LoginController.class));
        server.createContext("/api/dashboard/*", get(DashboardController.class));
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + PORT);
    }

    private static void registerServices() {
        // repository
        ServiceRegistry.register(DataSourceProvider.class, new DataSourceProviderImpl());
        TransactionContext.setDataSource(get(DataSourceProvider.class).getDataSource());

        ServiceRegistry.register(UserRepository.class, new UserRepositoryImpl());
        // service
        ServiceRegistry.register(LoginService.class, new LoginService(get(UserRepository.class)));
        // controllers
        ServiceRegistry.register(new LoginController(get(LoginService.class)));
        ServiceRegistry.register(new DashboardController());
    }

    private static void migrateDatabase() {
        DataSource ds = get(DataSourceProvider.class).getDataSource();
        Flyway.configure()
              .dataSource(ds)
              .locations("classpath:db/migration")
              .load()
              .migrate();
    }
}