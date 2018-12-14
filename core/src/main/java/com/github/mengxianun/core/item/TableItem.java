package com.github.mengxianun.core.item;

import com.github.mengxianun.core.schema.Table;
import com.google.common.base.Strings;

public class TableItem extends Item {

	// 表属性
	private Table table;
	// 自定义表达式. 可以是表名, 子查询等
	private String expression;
	// 表别名
	private String alias;
	// 自定义别名
	protected boolean customAlias;

	public TableItem(Table table) {
		this.table = table;
	}

	public TableItem(Table table, String alias) {
		this.table = table;
		if (Strings.isNullOrEmpty(alias)) {
			this.alias = getRandomAlias() + "_" + table.getName();
		} else {
			this.alias = alias;
			this.customAlias = true;
		}
	}

	public TableItem(String expression) {
		this.expression = expression;
	}

	public TableItem(String expression, String alias) {
		this.expression = expression;
		if (Strings.isNullOrEmpty(alias)) {
			this.alias = getRandomAlias() + "_" + table.getName();
		} else {
			this.alias = alias;
			this.customAlias = true;
		}
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
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

	public boolean isCustomAlias() {
		return customAlias;
	}

	public void setCustomAlias(boolean customAlias) {
		this.customAlias = customAlias;
	}

}
