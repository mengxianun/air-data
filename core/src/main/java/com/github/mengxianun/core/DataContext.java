package com.github.mengxianun.core;

import java.util.List;

import com.github.mengxianun.core.schema.Column;
import com.github.mengxianun.core.schema.Relationship;
import com.github.mengxianun.core.schema.Schema;
import com.github.mengxianun.core.schema.Table;

public interface DataContext {

	public Object action(Action action);

	public Object action(Action... actions);

	public Object executeNative(String script);

	public Object executeNative(Table table, String script);

	public List<Schema> getSchemas();

	public Schema getDefaultSchema();

	public Schema getSchema(String schemaName);

	public Table getTable(String tableName);

	public Table getTable(String schemaName, String tableName);

	public Column getColumn(String tableName, String columnName);

	public Column getColumn(String schemaName, String tableName, String columnName);

	public Relationship getRelationship(Table primaryTable, Table foreignTable);

	public String getIdentifierQuoteString();

	public Dialect getDialect();

}
