package com.github.mengxianun.core;

public enum ResultStatus {

	SUCCESS(200, "ok"), BAD_REQUEST(400, "Bad Request"), UNAUTHORIZED(401, "Unauthorized"), FORBIDDEN(403,
			"Forbidden"), SERVER_ERROR(500, "Internal Server Error");

	// 状态码
	private final int code;

	// 消息
	private final String message;

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

}
