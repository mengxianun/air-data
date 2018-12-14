package com.github.mengxianun.core.item;

import com.github.mengxianun.core.schema.Column;
import com.google.common.base.Strings;

public class ColumnItem extends Item {

	// 列属性
	protected Column column;
	// 自定义表达式. 可以是列名, 或者函数, 子查询等
	protected String expression;
	// 列别名
	protected String alias;
	// 所属操作表
	protected TableItem tableItem;
	// 自定义别名
	protected boolean customAlias;

	public ColumnItem(Column column) {
		this.column = column;
	}

	public ColumnItem(Column column, String alias) {
		this.column = column;
		if (Strings.isNullOrEmpty(alias)) {
			this.alias = getRandomAlias();
		} else {
			this.alias = alias;
			this.customAlias = true;
		}
	}

	public ColumnItem(Column column, TableItem tableItem) {
		this.column = column;
		this.tableItem = tableItem;
	}

	public ColumnItem(Column column, String alias, TableItem tableItem) {
		this(column, alias);
		this.tableItem = tableItem;
	}

	public ColumnItem(String expression) {
		this.expression = expression;
	}

	public ColumnItem(String expression, String alias) {
		this.expression = expression;
		if (Strings.isNullOrEmpty(alias)) {
			this.alias = getRandomAlias();
		} else {
			this.alias = alias;
			this.customAlias = true;
		}
	}

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public TableItem getTableItem() {
		return tableItem;
	}

	public void setTableItem(TableItem tableItem) {
		this.tableItem = tableItem;
	}

	public boolean isCustomAlias() {
		return customAlias;
	}

	public void setCustomAlias(boolean customAlias) {
		this.customAlias = customAlias;
	}

}
