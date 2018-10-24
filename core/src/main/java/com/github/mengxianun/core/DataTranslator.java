package com.github.mengxianun.core;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mengxianun.core.attributes.ConfigAttributes;
import com.github.mengxianun.core.exception.DataException;
import com.github.mengxianun.core.item.TableItem;
import com.github.mengxianun.core.resutset.DefaultDataResultSet;
import com.github.mengxianun.core.resutset.FailDataResultSet;
import com.github.mengxianun.core.schema.Table;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
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
		return translate(json, new String[] {});
	}

	@Override
	public DataResultSet translate(String json, String... filterExpressions) {
		long start = System.currentTimeMillis();
		JsonElement result = null;
		JsonObject jsonData = new com.google.gson.JsonParser().parse(json).getAsJsonObject();
		JsonParser jsonParser = new JsonParser(jsonData, this);
		jsonParser.parse();
		// -------------------------
		// 添加额外过滤条件, 待优化
		// -------------------------
		if (filterExpressions != null && filterExpressions.length > 0) {
			Arrays.asList(filterExpressions).forEach(jsonParser::addFilter);
		}
		try {
			if (jsonParser.isStruct()) {
				TableItem tableItem = jsonParser.getAction().getTableItems().get(0);
				Table table = tableItem.getTable();
				result = new Gson().toJsonTree(table);
			} else if (jsonParser.isTransaction()) {
				// to do
			} else if (jsonParser.isNative()) {
				TableItem tableItem = jsonParser.getAction().getTableItems().get(0);
				Table table = tableItem.getTable();
				result = jsonParser.getDataContext().executeNative(table, jsonParser.getNativeContent());
			} else if (jsonParser.isTemplate()) {
				// to do
			} else if (jsonParser.isResultFile()) {
				// to do
			} else {
				Action action = jsonParser.getAction();
				result = jsonParser.getDataContext().action(action);
				result = new DataRenderer().render(result, action);
			}
		} catch (DataException e) {
			logger.error(e.getMessage(), e.getCause());
			return new FailDataResultSet(e.getCode(), e.getMessage());
		} catch (Exception e) {
			return new FailDataResultSet(ResultStatus.TRANSLATION_FAILED);
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
