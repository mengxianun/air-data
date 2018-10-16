package com.github.mengxianun.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mengxianun.core.attributes.ConfigAttributes;
import com.github.mengxianun.core.exception.DataException;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Resources;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class AbstractTranslator implements Translator {

	private static final Logger logger = LoggerFactory.getLogger(AbstractTranslator.class);

	// 默认配置文件名
	protected static final String DEFAULT_CONFIG_FILE = "air.json";
	// 默认数据表配置路径
	protected static final String DEFAULT_TABLE_CONFIG_PATH = "tables";
	// 全局配置
	protected static final JsonObject configuration = new JsonObject();
	protected final Map<String, DataContext> dataContexts = new HashMap<>();
	private final List<DataContextFactory> factories = new ArrayList<>();

	static {
		// 初始化默认属性
		configuration.add(ConfigAttributes.DATASOURCES, JsonNull.INSTANCE);
		configuration.addProperty(ConfigAttributes.UPSERT, false);
		configuration.addProperty(ConfigAttributes.NATIVE, false);
		configuration.addProperty(ConfigAttributes.LOG, false);
		configuration.addProperty(ConfigAttributes.DEFAULT_DATASOURCE, "");
		configuration.addProperty(ConfigAttributes.TABLE_CONFIG_PATH, DEFAULT_TABLE_CONFIG_PATH);
		configuration.add(ConfigAttributes.TABLE_CONFIG, JsonNull.INSTANCE);
	}

	protected void parseConfiguration(URL configFileURL) {
		// 解析配置文件
		try {
			String configurationFileContent = Resources.toString(configFileURL, Charsets.UTF_8);
			JsonObject configurationJsonObject = new JsonParser().parse(configurationFileContent).getAsJsonObject();
			// 覆盖默认配置
			for (Entry<String, JsonElement> entry : configurationJsonObject.entrySet()) {
				configuration.add(entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			logger.error(String.format("configuration file [{}] parse failed", configFileURL), e);
			return;
		}

		discoverFromClasspath();

		JsonObject dataSourcesJsonObject = configuration.getAsJsonObject(ConfigAttributes.DATASOURCES);
		// 是否配置了默认数据源, 在没有配置默认数据源的情况下, 将第一个数据源设置为默认数据源
		if (!dataSourcesJsonObject.has(ConfigAttributes.DEFAULT_DATASOURCE)) {
			String defaultDataSourceName = dataSourcesJsonObject.keySet().iterator().next();
			configuration.addProperty(ConfigAttributes.DEFAULT_DATASOURCE, defaultDataSourceName);
		}
		
		for (Entry<String, JsonElement> entry : dataSourcesJsonObject.entrySet()) {
			String dataSourceName = entry.getKey();
			JsonObject dataSourceJsonObject = dataSourcesJsonObject.getAsJsonObject(dataSourceName);
			String type = dataSourceJsonObject.getAsJsonPrimitive(ConfigAttributes.DATASOURCE_TYPE).getAsString();
			for (DataContextFactory dataContextFactory : factories) {
				if (dataContextFactory.getType().equals(type)) {
					dataSourceJsonObject.remove(ConfigAttributes.DATASOURCE_TYPE);
					DataContext dataContext = dataContextFactory.create(dataSourceJsonObject);
					registerDataContext(dataSourceName, dataContext);
					break;
				}
			}
		}
	}

	@Override
	public void registerDataContext(String name, DataContext dataContext) {
		if (dataContexts.containsKey(name)) {
			throw new DataException(String.format("DataContext [%s] already exists", name));
		}
		dataContexts.put(name, dataContext);
		if (!configuration.has(ConfigAttributes.DEFAULT_DATASOURCE)
				|| Strings.isNullOrEmpty(configuration.get(ConfigAttributes.DEFAULT_DATASOURCE).getAsString())) {
			String defaultDataSourceName = dataContexts.keySet().iterator().next();
			configuration.addProperty(ConfigAttributes.DEFAULT_DATASOURCE, defaultDataSourceName);
		}
	}

	public void discoverFromClasspath() {
		final ServiceLoader<DataContextFactory> serviceLoader = ServiceLoader.load(DataContextFactory.class);
		for (DataContextFactory factory : serviceLoader) {
			addFactory(factory);
		}
	}

	public void addFactory(DataContextFactory factory) {
		factories.add(factory);
	}

	public DataContext getDataContext(String dataSourceName) {
		return dataContexts.get(dataSourceName);
	}

	public DataContext getDefaultDataContext() {
		return getDataContext(getDefaultDataSource());
	}

	public String getDefaultDataSource() {
		return configuration.getAsJsonPrimitive(ConfigAttributes.DEFAULT_DATASOURCE).getAsString();
	}

}
