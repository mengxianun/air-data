package com.github.mengxianun.core;

public class DefaultDialect implements Dialect {

	@Override
	public String getType() {
		return "";
	}

	@Override
	public boolean assignDatabase() {
		return false;
	}


}
