package com.github.mengxianun.core.item;

import com.github.mengxianun.core.schema.Column;

public class ColumnItem {

	// 列属性
	private Column column;

	// 自定义表达式. 可以是列名, 或者函数, 子查询等
	private String expression;

	// 列别名
	private String alias;

	public ColumnItem(Column column) {
		this.column = column;
	}

	public ColumnItem(Column column, String alias) {
		this.column = column;
		this.alias = alias;
	}

	public ColumnItem(String expression) {
		this.expression = expression;
	}

	public ColumnItem(String expression, String alias) {
		this.expression = expression;
		this.alias = alias;
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

}
