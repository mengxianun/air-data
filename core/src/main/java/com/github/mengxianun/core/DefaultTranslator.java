package com.github.mengxianun.core;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mengxianun.core.attributes.ConfigAttributes;
import com.github.mengxianun.core.exception.DataException;
import com.github.mengxianun.core.exception.PreHandlerException;
import com.github.mengxianun.core.item.TableItem;
import com.github.mengxianun.core.resutset.DefaultDataResultSet;
import com.github.mengxianun.core.resutset.FailDataResultSet;
import com.github.mengxianun.core.schema.Table;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class DefaultTranslator extends AbstractTranslator {

	private static final Logger logger = LoggerFactory.getLogger(DefaultTranslator.class);

	public DefaultTranslator() {
		this(configuration.getAsJsonPrimitive(ConfigAttributes.CONFIG_FILE).getAsString());
	}

	public DefaultTranslator(String configFile) {
		readConfig(configFile);
	}

	public DefaultTranslator(URL configFileURL) {
		readConfig(configFileURL);
	}

	@Override
	public DataResultSet translate(String json) {
		return translate(json, new String[] {});
	}
	@Override
	public DataResultSet translate(String json, String... filterExpressions) {

		long start = System.currentTimeMillis();

		try {
			// TODO step1. json解析处理
			JsonParser jsonParser = jsonParser(json);

			// TODO step2. 预处理（所有实现preHandler接口的预处理都会执行）
			super.preHandler(jsonParser);

			// TODO step3. 取数据
			JsonElement result = resultSet(jsonParser, filterExpressions);

			// TODO step4. 可以加后续处理
			long end = System.currentTimeMillis();

			// TODO step5. 结果封装
			return new DefaultDataResultSet(end - start, result);

		} catch (DataException e) {
			logger.error(e.getMessage(), e.getCause());
			return new FailDataResultSet(e.getCode(), e.getMessage());
		} catch (PreHandlerException e) {
			logger.error(e.getMessage(), e.getCause());
			return new FailDataResultSet(e.getCode(), e.getMessage());
		} catch (JsonSyntaxException e) {
			return new FailDataResultSet(ResultStatus.JSON_FORMAT_ERROR);
		} catch (Exception e) {
			return new FailDataResultSet(ResultStatus.TRANSLATION_FAILED);
		}
	}

	/**
	 * json解析，把参数字符串解析成json对象并进行封装
	 *
	 * @param json
	 * @return
	 * @throws JsonSyntaxException
	 */
	private JsonParser jsonParser(String json) throws JsonSyntaxException {
		JsonParser jsonParser;
		try {
			JsonObject jsonData = new com.google.gson.JsonParser().parse(json).getAsJsonObject();
			jsonParser = new JsonParser(jsonData, this);
			jsonParser.parse();
		} catch (Exception e) {
			throw e;
		}
		return jsonParser;
	}

	/**
	 * 取得数据
	 *
	 * @param jsonParser
	 * @param filterExpressions
	 * @return
	 */
	private JsonElement resultSet(JsonParser jsonParser, String... filterExpressions) {

		JsonElement result = null;

		try {
//			JsonObject jsonData = new com.google.gson.JsonParser().parse(json).getAsJsonObject();
//			JsonParser jsonParser = new JsonParser(jsonData, this);
//			jsonParser.parse();
			// -------------------------
			// 添加额外过滤条件, 待优化
			// -------------------------
			if (filterExpressions != null && filterExpressions.length > 0) {
				Arrays.asList(filterExpressions).forEach(jsonParser::addFilter);
			}
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
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return result;
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