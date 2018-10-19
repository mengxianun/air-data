package com.github.mengxianun.core;

public interface Dialect {

	public String getType();

	/**
	 * 是否指定数据库. 例: select database.table
	 * 
	 * @return
	 */
	public boolean assignDatabase();

	/**
	 * 是否用引用符号包裹表和列. 例: select "column" from "table"
	 * 
	 * @return
	 */
	public boolean quoteTable();

}
