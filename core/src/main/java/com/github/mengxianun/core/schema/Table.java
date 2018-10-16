package com.github.mengxianun.core.schema;

import java.util.List;

public interface Table extends Name {

	public Schema getSchema();

	public int getColumnCount();

	public List<Column> getColumns();

	public List<String> getColumnNames();

	public Column getColumnByName(String columnName);

	public List<Column> getPrimaryKeys();

	public String getRemarks();

}
