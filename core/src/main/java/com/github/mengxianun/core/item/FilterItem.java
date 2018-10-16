package com.github.mengxianun.core.item;

import java.util.ArrayList;
import java.util.List;

import com.github.mengxianun.core.json.Connector;
import com.github.mengxianun.core.json.Operator;
import com.github.mengxianun.core.schema.Column;

public class FilterItem {

	private Column column;

	// 自定义表达式. 可以是列名, 或者函数, 子查询等
	private String expression;

	private Object value;

	// 连接符, AND/OR
	private Connector connector;

	// 运算符
	private Operator operator;

	// 子条件
	private List<FilterItem> subFilterItems;

	public FilterItem() {
		this.connector = Connector.AND;
		this.subFilterItems = new ArrayList<>();
	}

	public FilterItem(Column column, Object value, Connector connector, Operator operator) {
		this();
		this.column = column;
		this.value = value;
		this.connector = connector;
		this.operator = operator;
	}

	public FilterItem(String expression, Object value, Connector connector, Operator operator) {
		this();
		this.expression = expression;
		this.value = value;
		this.connector = connector;
		this.operator = operator;
	}

	public static class Builder {
		private FilterItem filterItem;

		public Builder() {
			this.filterItem = new FilterItem();
		}

		public Builder column(Column column) {
			this.filterItem.column = column;
			return this;
		}

		public Builder value(Object value) {
			this.filterItem.value = value;
			return this;
		}

		public Builder connector(Connector connector) {
			this.filterItem.connector = connector;
			return this;
		}

		public Builder operator(Operator operator) {
			this.filterItem.operator = operator;
			return this;
		}

		public Builder subFilterItems(List<FilterItem> subFilterItems) {
			this.filterItem.subFilterItems = subFilterItems;
			return this;
		}

		public Builder expression(String expression) {
			this.filterItem.expression = expression;
			return this;
		}

		public FilterItem build() {
			return this.filterItem;
		}

	}

	public void addSubFilterItem(FilterItem subFilterItem) {
		this.subFilterItems.add(subFilterItem);
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

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Connector getConnector() {
		return connector;
	}

	public void setConnector(Connector connector) {
		this.connector = connector;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public List<FilterItem> getSubFilterItems() {
		return subFilterItems;
	}

	public void setSubFilterItems(List<FilterItem> subFilterItems) {
		this.subFilterItems = subFilterItems;
	}

}
