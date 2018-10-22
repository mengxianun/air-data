package com.github.mengxianun.core;

/**
 * 结果状态
 * 
 * @author mengxiangyun
 *
 */
public enum ResultStatus {

	// 成功
	SUCCESS(0, "ok"),

	/**
	 * 权限错误
	 */
	AUTHENTICATION(10100, ""),

	/**
	 * 数据源错误
	 */
	// -- 预留
	DATASOURCE(10200, ""),
	// 数据源不存在
	DATASOURCE_NOT_EXIST(10201, "Data source [%s] does not exist"),
	// 数据库表不存在
	DATASOURCE_TABLE_EXIST(10202, "Database tables [%s] do not exist"),
	// SQL 执行失败
	DATASOURCE_SQL_FAILED(10203, "SQL statement execution failed. SQL: %s"),

	/**
	 * JSON 错误
	 */
	//
	JSON(10400, ""),
	// 不支持的属性
	JSON_UNSUPPORTED_ATTRIBUTE(10401, "Unsupported attributes [%s]"),
	// JSON 属性格式错误
	JSON_ATTRIBUTE_FORMAT_ERROR(10402, "Json attribute [%s] format error, %s."),

	/*
	 * 其他错误
	 */
	// 翻译失败
	TRANSLATION_FAILED(10800, "Json translation failed"),
	// 原生语句执行失败
	NATIVE_FAILED(10801, "Native statement execution failed"),
	
	/*
	 * 系统错误
	 */
	SYSTEM_ERROR(10900, "System error: %s");

	// 状态码
	private int code;
	// 消息
	private String message;

	ResultStatus(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int code() {
		return this.code;
	}

	public String message() {
		return this.message;
	}

	public ResultStatus fill(Object... args) {
		this.message = String.format(this.message, args);
		return this;
	}

}
