package com.calculatorify.model.repository;

import com.google.common.io.Resources;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Anton Gorokh
 */
public class DataSourceProviderImpl implements DataSourceProvider {

	private static final HikariDataSource DATA_SOURCE;

	static {
		try (InputStream in = Resources.getResource("db.properties").openStream()) {
			Properties p = new Properties();
			if (in == null) throw new IllegalStateException("db.properties not found");
			p.load(in);
			HikariConfig cfg = new HikariConfig();
			cfg.setJdbcUrl(p.getProperty("jdbc.url"));
			cfg.setUsername(p.getProperty("jdbc.user"));
			cfg.setPassword(p.getProperty("jdbc.password"));
			cfg.setMaximumPoolSize(10);
			DATA_SOURCE = new HikariDataSource(cfg);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	@Override
	public DataSource getDataSource() {
		return DATA_SOURCE;
	}
}
