package com.github.mengxianun.jdbc.dialect;

import com.github.mengxianun.core.Dialect;
import com.google.auto.service.AutoService;

@AutoService(Dialect.class)
public class MariaDBDialect implements Dialect {

	@Override
	public String getType() {
		return "mariadb";
	}

}
