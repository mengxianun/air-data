package com.github.mengxianun.core.item;

import com.github.mengxianun.core.json.Order;
import com.github.mengxianun.core.schema.Column;

public class OrderItem {

	private Column column;

	// 自定义表达式. 可以是列名, 或者函数, 子查询等
	private String expression;

	private Order order;

	public OrderItem(Column column, Order order) {
		this.column = column;
		this.order = order;
	}

	public OrderItem(String expression, Order order) {
		this.expression = expression;
		this.order = order;
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

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

}
