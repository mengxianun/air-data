package com.github.mengxianun.core.item;

import java.util.ArrayList;
import java.util.List;

import com.github.mengxianun.core.json.Connector;
import com.github.mengxianun.core.json.Operator;

public class FilterItem extends Item {

	private static final long serialVersionUID = 1L;
	// 条件列
	private ColumnItem columnItem;
	// 条件值
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

	public FilterItem(ColumnItem columnItem, Object value, Connector connector, Operator operator) {
		this();
		this.columnItem = columnItem;
		this.value = value;
		this.connector = connector;
		this.operator = operator;
	}

	public void addSubFilterItem(FilterItem subFilterItem) {
		this.subFilterItems.add(subFilterItem);
	}

	public ColumnItem getColumnItem() {
		return columnItem;
	}

	public void setColumnItem(ColumnItem columnItem) {
		this.columnItem = columnItem;
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
