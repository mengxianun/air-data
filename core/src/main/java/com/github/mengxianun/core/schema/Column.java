package com.github.mengxianun.core.schema;

public interface Column extends Name {

	public Table getTable();

	public Boolean isNullable();

	public String getRemarks();

	public Integer getColumnSize();

	public boolean isPrimaryKey();

}
