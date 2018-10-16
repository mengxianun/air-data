package com.github.mengxianun.core.item;

import com.github.mengxianun.core.schema.Column;

public class GroupItem {

	private Column column;

	// 自定义表达式. 可以是列名, 或者函数, 子查询等
	private String expression;

	public GroupItem(Column column) {
		this.column = column;
	}

	public GroupItem(String expression) {
		this.expression = expression;
	}

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

}
