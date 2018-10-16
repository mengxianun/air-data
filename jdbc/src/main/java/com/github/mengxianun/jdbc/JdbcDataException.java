package com.github.mengxianun.jdbc;

public class JdbcDataException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JdbcDataException() {
		super();
	}

	public JdbcDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public JdbcDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public JdbcDataException(String message) {
		super(message);
	}

	public JdbcDataException(Throwable cause) {
		super(cause);
	}

}
