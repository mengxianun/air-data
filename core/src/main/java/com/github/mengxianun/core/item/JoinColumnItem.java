package com.github.mengxianun.core.item;

import java.util.ArrayList;
import java.util.List;

import com.github.mengxianun.core.attributes.AssociationType;
import com.github.mengxianun.core.schema.Column;
import com.github.mengxianun.core.schema.Table;

public class JoinColumnItem extends ColumnItem {

	private AssociationType associationType;
	// 关联的所有父表
	private List<Table> parentTables = new ArrayList<>();

	public JoinColumnItem(Column column) {
		super(column);
	}

	public JoinColumnItem(Column column, String alias) {
		super(column, alias);
	}

	public JoinColumnItem(Column column, TableItem tableItem) {
		super(column, tableItem);
	}

	public JoinColumnItem(Column column, String alias, TableItem tableItem) {
		super(column, alias, tableItem);
	}

	public JoinColumnItem(String expression) {
		super(expression);
	}

	public JoinColumnItem(String expression, String alias) {
		super(expression, alias);
	}

	public void addParentTable(Table parentTable) {
		parentTables.add(parentTable);
	}

	public AssociationType getAssociationType() {
		return associationType;
	}

	public void setAssociationType(AssociationType associationType) {
		this.associationType = associationType;
	}

	public List<Table> getParentTables() {
		return parentTables;
	}

	public void setParentTables(List<Table> parentTables) {
		this.parentTables = parentTables;
	}

}
