package com.github.mengxianun.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.mengxianun.core.DataContextFactory;
import com.google.auto.service.AutoService;
import com.google.gson.JsonObject;

@AutoService(DataContextFactory.class)
public final class JdbcDataContextFactory implements DataContextFactory {

	private static final String DB_URL = "url";
	private static final String DB_USERNAME = "username";
	private static final String DB_PASSWORD = "password";

	@Override
	public String getType() {
		return "jdbc";
	}

	@Override
	public JdbcDataContext create(JsonObject dataSourceJsonObject) {
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setUrl(dataSourceJsonObject.getAsJsonPrimitive(DB_URL).getAsString());
		druidDataSource.setUsername(dataSourceJsonObject.getAsJsonPrimitive(DB_USERNAME).getAsString());
		druidDataSource.setPassword(dataSourceJsonObject.getAsJsonPrimitive(DB_PASSWORD).getAsString());
		// 构建DataContext
		return new JdbcDataContext(druidDataSource);
	}

}
