package com.github.mengxianun.core;

import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mengxianun.core.attributes.ConfigAttributes;
import com.github.mengxianun.core.exception.DataException;
import com.github.mengxianun.core.item.TableItem;
import com.github.mengxianun.core.schema.Table;
import com.google.gson.JsonObject;

public class DataTranslator extends AbstractTranslator {

	private static final Logger logger = LoggerFactory.getLogger(DataTranslator.class);

	public DataTranslator() {
		this(configuration.getAsJsonPrimitive(ConfigAttributes.CONFIG_FILE).getAsString());
	}

	public DataTranslator(String configFile) {
		readConfig(configFile);
	}

	public DataTranslator(URL configFileURL) {
		readConfig(configFileURL);
	}
	
	@Override
	public DataResultSet translate(String json) {
		long start = System.currentTimeMillis();
		Object result = null;
		JsonObject jsonData = new com.google.gson.JsonParser().parse(json).getAsJsonObject();
		JsonParser jsonParser = new JsonParser(jsonData, this);
		jsonParser.parse();
		try {
			if (jsonParser.isStruct()) {
				TableItem tableItem = jsonParser.getAction().getTableItems().get(0);
				Table table = tableItem.getTable();
				result = table;
			} else if (jsonParser.isTransaction()) {
				// to do
			} else if (jsonParser.isNative()) {
				TableItem tableItem = jsonParser.getAction().getTableItems().get(0);
				Table table = tableItem.getTable();
				result = jsonParser.getDataContext().executeNative(table, jsonParser.getNativeContent());
			} else {
				Action action = jsonParser.getAction();
				result = jsonParser.getDataContext().action(action);
				//
				result = new DataRenderer().render(result, action);
			}
		} catch (DataException e) {
			return new DefaultDataResultSet(e.getResultStatus());
		} catch (Exception e) {
			return new DefaultDataResultSet(ResultStatus.SERVER_ERROR.code(), e.getMessage());
		}
		long end = System.currentTimeMillis();
		long took = end - start;
		return new DefaultDataResultSet(took, result);
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
