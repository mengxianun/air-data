package com.github.mengxianun.core;

import java.util.List;

import com.github.mengxianun.core.schema.Column;
import com.github.mengxianun.core.schema.Schema;
import com.github.mengxianun.core.schema.Table;

public abstract class AbstractDataContext implements DataContext {

	protected MetaData metaData = new MetaData();

	protected Dialect dialect;

	protected abstract void initializeMetaData();

	@Override
	public DataResultSet executeNative(Table table, String script) {
		return executeNative(script);
	}

	@Override
	public List<Schema> getSchemas() {
		return metaData.getSchemas();
	}

	@Override
	public Schema getDefaultSchema() {
		return metaData.getDefaultSchema();
	}

	@Override
	public Schema getSchema(String schemaName) {
		return metaData.getSchema(schemaName);
	}

	@Override
	public Table getTable(String tableName) {
		return metaData.getTable(tableName);
	}

	@Override
	public Table getTable(String schemaName, String tableName) {
		return metaData.getTable(schemaName, tableName);
	}

	@Override
	public Column getColumn(String tableName, String columnName) {
		return metaData.getColumn(tableName, columnName);
	}

	@Override
	public Column getColumn(String schemaName, String tableName, String columnName) {
		return metaData.getColumn(schemaName, tableName, columnName);
	}

	@Override
	public String getIdentifierQuoteString() {
		return metaData.getIdentifierQuoteString();
	}

	public Dialect getDialect() {
		return dialect;
	}

}
