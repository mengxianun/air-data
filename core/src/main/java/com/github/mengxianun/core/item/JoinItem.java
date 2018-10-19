package com.github.mengxianun.core.item;

import com.github.mengxianun.core.json.JoinType;

public class JoinItem {

	private ColumnItem leftColumn;

	private ColumnItem rightColumn;

	private JoinType joinType;

	public JoinItem(ColumnItem leftColumn, ColumnItem rightColumn) {
		this.leftColumn = leftColumn;
		this.rightColumn = rightColumn;
	}

	public JoinItem(ColumnItem leftColumn, ColumnItem rightColumn, JoinType joinType) {
		this.leftColumn = leftColumn;
		this.rightColumn = rightColumn;
		this.joinType = joinType;
	}

	public ColumnItem getLeftColumn() {
		return leftColumn;
	}

	public void setLeftColumn(ColumnItem leftColumn) {
		this.leftColumn = leftColumn;
	}

	public ColumnItem getRightColumn() {
		return rightColumn;
	}

	public void setRightColumn(ColumnItem rightColumn) {
		this.rightColumn = rightColumn;
	}


	public JoinType getJoinType() {
		return joinType;
	}


	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}

}
