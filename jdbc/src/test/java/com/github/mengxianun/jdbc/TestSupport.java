package com.github.mengxianun.jdbc;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;

import org.h2.tools.RunScript;
import org.junit.jupiter.api.BeforeAll;

import com.github.mengxianun.core.DataTranslator;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.JsonObject;

public class TestSupport {
	
	public static DataTranslator translator = new DataTranslator();
	public static final String DB_DRIVER_CLASS_NAME = "org.h2.Driver";
	public static final String DB_URL = "jdbc:h2:~/air_jdbc";
	public static final String DB_USERNAME = "test";
	public static final String DB_PASSWORD = "123456";
	public static final String DATASOURCE_NAME = "ds";
	public static final String DATABASE_INIT_SCRIPT = "test.sql";
	
	public static boolean databaseCreated = false;

	public static void createDataContext() {
		JsonObject dataSourceJsonObject = new JsonObject();
		dataSourceJsonObject.addProperty("url", DB_URL);
		dataSourceJsonObject.addProperty("username", DB_USERNAME);
		dataSourceJsonObject.addProperty("password", DB_PASSWORD);
		JdbcDataContext jdbcDataContext = new JdbcDataContextFactory().create(dataSourceJsonObject);
		translator.registerDataContext(DATASOURCE_NAME, jdbcDataContext);
	}
	
	public static void initDatabase() {
		try {
			String scriptPath = TestSupport.class.getClassLoader().getResource(DATABASE_INIT_SCRIPT).toString();
			RunScript.execute(DB_URL, DB_USERNAME, DB_PASSWORD, scriptPath, Charset.defaultCharset(), false);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@BeforeAll
	static void initAll() {
		if (databaseCreated) {
			return;
		}
		try {
			Class.forName(DB_DRIVER_CLASS_NAME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		initDatabase();
		createDataContext();
		databaseCreated = true;
	}

	public String readJson(String jsonFile) {
		URL url = Resources.getResource(jsonFile);
		try {
			return Resources.toString(url, Charsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

}
