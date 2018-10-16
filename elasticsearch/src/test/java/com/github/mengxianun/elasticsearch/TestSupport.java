package com.github.mengxianun.elasticsearch;

import java.io.IOException;
import java.net.URL;

import com.github.mengxianun.core.DataTranslator;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.JsonObject;

public class TestSupport {

	public static DataTranslator translator = new DataTranslator();
	public static final String DB_URL = "jdbc:elasticsearch://192.168.201.171:9300/";
	public static final String DATASOURCE_NAME = "ds";

	public static void createDataContext() {
		JsonObject dataSourceJsonObject = new JsonObject();
		dataSourceJsonObject.addProperty("url", DB_URL);
		ElasticsearchDataContext dataContext = new ElasticsearchDataContextFactory().create(dataSourceJsonObject);
		translator.registerDataContext(DATASOURCE_NAME, dataContext);
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
