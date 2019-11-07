package com.example.core.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据源路由，实现数据源的动态切换
 * @author wuwei
 */
public class RoutingDataSource extends AbstractRoutingDataSource {

	@Override
	public Connection getConnection() throws SQLException {
		Connection connection = super.getConnection();
		connection.prepareStatement("set names utf8mb4").executeQuery();
		return connection;
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return DatasourceHolder.getDatasourceKey();
	}

}