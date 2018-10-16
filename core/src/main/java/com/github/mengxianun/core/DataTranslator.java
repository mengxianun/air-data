package com.github.mengxianun.core;

import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mengxianun.core.item.TableItem;
import com.github.mengxianun.core.schema.Table;
import com.google.common.io.Resources;
import com.google.gson.JsonObject;

public class DataTranslator extends AbstractTranslator {

	private static final Logger logger = LoggerFactory.getLogger(DataTranslator.class);

	public DataTranslator() {
		this(DEFAULT_CONFIG_FILE);
	}

	public DataTranslator(String configFile) {
		URL configFileURL = null;
		try {
			configFileURL = Resources.getResource(configFile);
		} catch (Exception e) {
			logger.error("config file [{}] parse error", configFile);
		}
		parseConfiguration(configFileURL);
	}

	public DataTranslator(URL configFileURL) {
		parseConfiguration(configFileURL);
	}
	
	@Override
	public DataResultSet translate(String json) {
		DataResultSet dataResultSet = null;
		JsonObject jsonData = new com.google.gson.JsonParser().parse(json).getAsJsonObject();
		JsonParser jsonParser = new JsonParser(jsonData, this);
		jsonParser.parse();

		if (jsonParser.isStruct()) {
			TableItem tableItem = jsonParser.getAction().getTableItems().get(0);
			Table table = tableItem.getTable();
			dataResultSet = new DefaultDataResultSet(table);
		} else if (jsonParser.isTransaction()) {
			// to do
		} else if (jsonParser.isNative()) {
			TableItem tableItem = jsonParser.getAction().getTableItems().get(0);
			Table table = tableItem.getTable();
			return jsonParser.getDataContext().executeNative(table, jsonParser.getNativeContent());
		} else {
			Action action = jsonParser.getAction();
			dataResultSet = jsonParser.getDataContext().action(action);
		}
		return dataResultSet;
	}

	@Override
	public String translateToString(String json) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream translateToStream(String json) {
		// TODO Auto-generated method stub
		return null;
	}



}
