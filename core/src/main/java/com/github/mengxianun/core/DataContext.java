package com.github.mengxianun.core;

import java.util.List;

import com.github.mengxianun.core.schema.Column;
import com.github.mengxianun.core.schema.Schema;
import com.github.mengxianun.core.schema.Table;

public interface DataContext {

	public DataResultSet action(Action action);

	public DataResultSet action(Action... actions);

	public DataResultSet executeNative(String script);

	public DataResultSet executeNative(Table table, String script);

	public List<Schema> getSchemas();

	public Schema getDefaultSchema();

	public Schema getSchema(String schemaName);

	public Table getTable(String tableName);

	public Table getTable(String schemaName, String tableName);

	public Column getColumn(String tableName, String columnName);

	public Column getColumn(String schemaName, String tableName, String columnName);

	public String getIdentifierQuoteString();

	public Dialect getDialect();

}
