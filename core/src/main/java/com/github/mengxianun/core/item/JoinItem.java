package com.github.mengxianun.core.item;

import com.github.mengxianun.core.json.JoinType;
import com.github.mengxianun.core.schema.Column;

public class JoinItem {

	private Column column1;

	private Column column2;

	private JoinType joinType;

	public JoinItem(Column column1, Column column2) {
		this.column1 = column1;
		this.column2 = column2;
	}

	public Column getColumn1() {
		return column1;
	}

	public void setColumn1(Column column1) {
		this.column1 = column1;
	}

	public Column getColumn2() {
		return column2;
	}

	public void setColumn2(Column column2) {
		this.column2 = column2;
	}

	public JoinType getJoinType() {
		return joinType;
	}

	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}

}
