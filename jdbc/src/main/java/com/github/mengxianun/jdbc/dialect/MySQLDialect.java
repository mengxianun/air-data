package com.github.mengxianun.jdbc.dialect;

import com.github.mengxianun.core.Dialect;
import com.google.auto.service.AutoService;

@AutoService(Dialect.class)
public class MySQLDialect implements Dialect {

	@Override
	public String getType() {
		return "mysql";
	}

	@Override
	public boolean assignDatabase() {
		return true;
	}

	@Override
	public boolean quoteTable() {
		return true;
	}

}
