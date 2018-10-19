package com.github.mengxianun.core.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.mengxianun.core.attributes.AssociationType;
import com.github.mengxianun.core.attributes.TableConfigAttributes;
import com.google.gson.JsonObject;

public class DefaultTable implements Table {

	private String name;
	private Schema schema;
	private String remarks;
	private List<Column> columns;
	// 自定义配置i信息
	private JsonObject config = new JsonObject();

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

	@Override
	public List<Relationship> getRelationships() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Relationship getRelationship(Table foreignTable) {
		// 从数据表配置中查找关联
		if (config.has(TableConfigAttributes.COLUMNS)) {
			JsonObject columnsConfig = config.getAsJsonObject(TableConfigAttributes.COLUMNS);
			for (String columnName : columnsConfig.keySet()) {
				JsonObject columnConfig = columnsConfig.getAsJsonObject(columnName);
				if (columnConfig.has(TableConfigAttributes.COLUMN_ASSOCIATION)) {
					JsonObject associationConfig = columnConfig
							.getAsJsonObject(TableConfigAttributes.COLUMN_ASSOCIATION);
					String targetTableName = associationConfig
							.getAsJsonPrimitive(TableConfigAttributes.ASSOCIATION_TARGET_TABLE).getAsString();
					if (foreignTable.getName().equalsIgnoreCase(targetTableName)) {
						String targetColumnName = associationConfig
								.getAsJsonPrimitive(TableConfigAttributes.ASSOCIATION_TARGET_COLUMN).getAsString();
						AssociationType associationType;
						if (associationConfig.has(TableConfigAttributes.ASSOCIATION_TYPE)) {
							String associationTypeString = associationConfig
									.getAsJsonPrimitive(TableConfigAttributes.ASSOCIATION_TYPE).getAsString();
							associationType = AssociationType.from(associationTypeString);
						} else {
							associationType = AssociationType.ONE_TO_ONE;
						}
						Column primaryColumn = getColumnByName(columnName);
						Column foreignColumn = foreignTable.getColumnByName(targetColumnName);
						return new DefaultRelationship(primaryColumn, foreignColumn, associationType);
					}
				}
			}
		}
		return null;
	}


	@Override
	public AssociationType getAssociationType(Table foreignTable) {
		Relationship relationship = getRelationship(foreignTable);
		if (relationship == null) {
			relationship = foreignTable.getRelationship(this);
			if (relationship == null) {
				return AssociationType.ONE_TO_ONE;
			}
			return relationship.getAssociationType().reverse();
		} else {
			return relationship.getAssociationType();
		}
	}

	@Override
	public JsonObject getConfig() {
		return config;
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

	public void setConfig(JsonObject config) {
		this.config = config;
	}

}
