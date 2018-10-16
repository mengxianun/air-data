package com.github.mengxianun.elasticsearch.processor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.dbutils.BasicRowProcessor;

public class ElasticsearchRowProcessor extends BasicRowProcessor {

	@Override
	public Map<String, Object> toMap(ResultSet rs) throws SQLException {
		Map<String, Object> result = new CaseInsensitiveHashMap();
		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();

		for (int i = 1; i <= cols; i++) {
			String columnName = rsmd.getColumnLabel(i);
			if (null == columnName || 0 == columnName.length()) {
				columnName = rsmd.getColumnName(i);
			}
			result.put(columnName, rs.getObject(i - 1));
		}

		return result;
	}

	private static class CaseInsensitiveHashMap extends LinkedHashMap<String, Object> {
		private final Map<String, String> lowerCaseMap = new HashMap<String, String>();

		private static final long serialVersionUID = -2848100435296897392L;

		@Override
		public boolean containsKey(Object key) {
			Object realKey = lowerCaseMap.get(key.toString().toLowerCase(Locale.ENGLISH));
			return super.containsKey(realKey);
		}

		@Override
		public Object get(Object key) {
			Object realKey = lowerCaseMap.get(key.toString().toLowerCase(Locale.ENGLISH));
			return super.get(realKey);
		}

		@Override
		public Object put(String key, Object value) {
			Object oldKey = lowerCaseMap.put(key.toLowerCase(Locale.ENGLISH), key);
			Object oldValue = super.remove(oldKey);
			super.put(key, value);
			return oldValue;
		}

		@Override
		public void putAll(Map<? extends String, ?> m) {
			for (Map.Entry<? extends String, ?> entry : m.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				this.put(key, value);
			}
		}

		@Override
		public Object remove(Object key) {
			Object realKey = lowerCaseMap.remove(key.toString().toLowerCase(Locale.ENGLISH));
			return super.remove(realKey);
		}
	}

}
