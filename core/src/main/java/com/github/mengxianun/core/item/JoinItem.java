package com.github.mengxianun.core.item;

import java.util.List;

import com.github.mengxianun.core.json.JoinType;

public class JoinItem {

	private List<ColumnItem> leftColumns;

	private List<ColumnItem> rightColumns;

	private JoinType joinType;

	public JoinItem(List<ColumnItem> leftColumns, List<ColumnItem> rightColumns) {
		this.leftColumns = leftColumns;
		this.rightColumns = rightColumns;
	}

	public JoinItem(List<ColumnItem> leftColumns, List<ColumnItem> rightColumns, JoinType joinType) {
		this.leftColumns = leftColumns;
		this.rightColumns = rightColumns;
		this.joinType = joinType;
	}

	public List<ColumnItem> getLeftColumns() {
		return leftColumns;
	}

	public void setLeftColumns(List<ColumnItem> leftColumns) {
		this.leftColumns = leftColumns;
	}

	public List<ColumnItem> getRightColumns() {
		return rightColumns;
	}

	public void setRightColumns(List<ColumnItem> rightColumns) {
		this.rightColumns = rightColumns;
	}

	public JoinType getJoinType() {
		return joinType;
	}

	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}

}
