package com.calculatorify.model.repository;

import javax.sql.DataSource;

/**
 * @author Anton Gorokh
 */
public interface DataSourceProvider {
	DataSource getDataSource();
}
