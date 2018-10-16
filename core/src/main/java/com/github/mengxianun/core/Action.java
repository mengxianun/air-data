package com.github.mengxianun.core;

import java.util.ArrayList;
import java.util.List;

import com.github.mengxianun.core.item.ColumnItem;
import com.github.mengxianun.core.item.FilterItem;
import com.github.mengxianun.core.item.GroupItem;
import com.github.mengxianun.core.item.JoinItem;
import com.github.mengxianun.core.item.LimitItem;
import com.github.mengxianun.core.item.OrderItem;
import com.github.mengxianun.core.item.TableItem;
import com.github.mengxianun.core.item.ValueItem;
import com.github.mengxianun.core.json.Operation;

public class Action {

	private Operation operation;

	private DataContext dataContext;

	private List<TableItem> tableItems;
	private List<ColumnItem> columnItems;
	private List<JoinItem> joinItems;
	private List<FilterItem> filterItems;
	private List<GroupItem> groupItems;
	private List<OrderItem> orderItems;
	private LimitItem limitItem;
	private List<ValueItem> valueItems;

	public Action() {
		this.tableItems = new ArrayList<>();
		this.columnItems = new ArrayList<>();
		this.joinItems = new ArrayList<>();
		this.filterItems = new ArrayList<>();
		this.groupItems = new ArrayList<>();
		this.orderItems = new ArrayList<>();
		this.valueItems = new ArrayList<>();
	}

	public Action(Operation operation) {
		this();
		this.operation = operation;

	}

	public void addTableItem(TableItem tableItem) {
		this.tableItems.add(tableItem);
	}

	public void addColumnItem(ColumnItem columnItem) {
		this.columnItems.add(columnItem);
	}

	public void addJoinItem(JoinItem joinItem) {
		this.joinItems.add(joinItem);
	}

	public void addFilterItem(FilterItem filterItem) {
		this.filterItems.add(filterItem);
	}

	public void addGroupItem(GroupItem groupItem) {
		this.groupItems.add(groupItem);
	}

	public void addOrderItem(OrderItem orderItem) {
		this.orderItems.add(orderItem);
	}

	public void addLimitItem(LimitItem limitItem) {
		this.limitItem = limitItem;
	}

	public void addValueItem(ValueItem valueItem) {
		this.valueItems.add(valueItem);
	}

	public boolean isDetail() {
		return operation != null && operation == Operation.DETAIL;
	}

	public boolean isSelect() {
		return operation != null && (operation == Operation.QUERY || operation == Operation.SELECT);
	}

	public boolean isUpdate() {
		return operation != null && operation == Operation.UPDATE;
	}

	public boolean isInsert() {
		return operation != null && operation == Operation.INSERT;
	}

	public boolean isDelete() {
		return operation != null && operation == Operation.DELETE;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public DataContext getDataContext() {
		return dataContext;
	}

	public void setDataContext(DataContext dataContext) {
		this.dataContext = dataContext;
	}

	public List<TableItem> getTableItems() {
		return tableItems;
	}

	public void setTableItems(List<TableItem> tableItems) {
		this.tableItems = tableItems;
	}

	public List<ColumnItem> getColumnItems() {
		return columnItems;
	}

	public void setColumnItems(List<ColumnItem> columnItems) {
		this.columnItems = columnItems;
	}

	public List<JoinItem> getJoinItems() {
		return joinItems;
	}

	public void setJoinItems(List<JoinItem> joinItems) {
		this.joinItems = joinItems;
	}

	public List<FilterItem> getFilterItems() {
		return filterItems;
	}

	public void setFilterItems(List<FilterItem> filterItems) {
		this.filterItems = filterItems;
	}

	public List<GroupItem> getGroupItems() {
		return groupItems;
	}

	public void setGroupItems(List<GroupItem> groupItems) {
		this.groupItems = groupItems;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public LimitItem getLimitItem() {
		return limitItem;
	}

	public void setLimitItem(LimitItem limitItem) {
		this.limitItem = limitItem;
	}

	public List<ValueItem> getValueItems() {
		return valueItems;
	}

	public void setValueItems(List<ValueItem> valueItems) {
		this.valueItems = valueItems;
	}

}
