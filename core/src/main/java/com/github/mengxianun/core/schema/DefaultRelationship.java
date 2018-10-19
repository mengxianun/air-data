package com.github.mengxianun.core.schema;

import com.github.mengxianun.core.attributes.AssociationType;

public class DefaultRelationship implements Relationship {

	private Column primaryColumn;
	private Column foreignColumn;
	private AssociationType associationType;

	public DefaultRelationship(Column primaryColumn, Column foreignColumn) {
		this.primaryColumn = primaryColumn;
		this.foreignColumn = foreignColumn;
		this.associationType = AssociationType.ONE_TO_ONE;
	}

	public DefaultRelationship(Column primaryColumn, Column foreignColumn, AssociationType associationType) {
		this.primaryColumn = primaryColumn;
		this.foreignColumn = foreignColumn;
		this.associationType = associationType;
	}

	@Override
	public Table getPrimaryTable() {
		return primaryColumn.getTable();
	}

	@Override
	public Column getPrimaryColumn() {
		return primaryColumn;
	}

	@Override
	public Table getForeignTable() {
		return foreignColumn.getTable();
	}

	@Override
	public Column getForeignColumn() {
		return foreignColumn;
	}

	@Override
	public AssociationType getAssociationType() {
		return associationType;
	}

	public void setPrimaryColumn(Column primaryColumn) {
		this.primaryColumn = primaryColumn;
	}

	public void setForeignColumn(Column foreignColumn) {
		this.foreignColumn = foreignColumn;
	}

	public void setAssociationType(AssociationType associationType) {
		this.associationType = associationType;
	}

}
