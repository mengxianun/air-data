package com.github.mengxianun.core.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultTable implements Table {

	private String name;

	private Schema schema;

	private String remarks;

	private List<Column> columns;

	public DefaultTable() {
		this.columns = new ArrayList<>();
	}

	public DefaultTable(String name) {
		this();
		this.name = name;
	}

	public DefaultTable(String name, Schema schema) {
		this();
		this.name = name;
		this.schema = schema;
	}

	public DefaultTable(String name, Schema schema, String remarks) {
		this();
		this.name = name;
		this.schema = schema;
		this.remarks = remarks;
	}

	public DefaultTable(String name, Schema schema, String remarks, List<Column> columns) {
		this.name = name;
		this.schema = schema;
		this.remarks = remarks;
		this.columns = columns;

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Schema getSchema() {
		return schema;
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public List<Column> getColumns() {
		return columns;
	}

	@Override
	public List<String> getColumnNames() {
		return columns.stream().map(column -> column.getName()).collect(Collectors.toList());
	}

	@Override
	public Column getColumnByName(String columnName) {
		if (columnName == null) {
			return null;
		}

		List<Column> foundColumns = new ArrayList<>();
		for (Column column : columns) {
			if (column.getName().equalsIgnoreCase(columnName)) {
				foundColumns.add(column);
			}
		}

		if (foundColumns.isEmpty()) {
			return null;
		} else if (foundColumns.size() == 1) {
			return foundColumns.get(0);
		}

		for (Column column : foundColumns) {
			if (column.getName().equals(columnName)) {
				return column;
			}
		}

		return foundColumns.get(0);
	}

	@Override
	public List<Column> getPrimaryKeys() {
		return null;
	}

	@Override
	public String getRemarks() {
		return remarks;
	}

	public void addColumn(Column column) {
		columns.add(column);
	}

	public void removeColumn(Column column) {
		columns.remove(column);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

}
