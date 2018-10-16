package com.github.mengxianun.elasticsearch;

public class ElasticsearchDataException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ElasticsearchDataException() {
		super();
	}

	public ElasticsearchDataException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ElasticsearchDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public ElasticsearchDataException(String message) {
		super(message);
	}

	public ElasticsearchDataException(Throwable cause) {
		super(cause);
	}


}
