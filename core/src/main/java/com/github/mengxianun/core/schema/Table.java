package com.github.mengxianun.core.schema;

import java.util.List;

import com.github.mengxianun.core.attributes.AssociationType;
import com.google.gson.JsonObject;

public interface Table extends Name {

	public Schema getSchema();

	public int getColumnCount();

	public List<Column> getColumns();

	public List<String> getColumnNames();

	public Column getColumnByName(String columnName);

	public List<Column> getPrimaryKeys();

	public String getRemarks();

	public List<Relationship> getRelationships();

	/**
	 * 获取配置的关联关系
	 * 
	 * @param foreignTable
	 * @return
	 */
	public Relationship getRelationship(Table foreignTable);

	/**
	 * 获取关联关系
	 * 
	 * @param foreignTable
	 * @return
	 */
	public AssociationType getAssociationType(Table foreignTable);

	public JsonObject getConfig();

	default void setConfig(JsonObject config) {
	}

}
