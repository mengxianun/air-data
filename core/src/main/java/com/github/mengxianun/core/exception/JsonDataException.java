package com.github.mengxianun.core.exception;

/**
 * Json 数据结构异常
 * 
 * @author mengxiangyun
 *
 */
public class JsonDataException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JsonDataException() {
		super();
	}

	public JsonDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public JsonDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public JsonDataException(String message) {
		super(message);
	}

	public JsonDataException(Throwable cause) {
		super(cause);
	}


}
