package com.github.mengxianun.core.exception;

import com.github.mengxianun.core.ResultStatus;

public class DataException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	protected ResultStatus resultStatus;

	public DataException() {
		super();
	}

	public DataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DataException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataException(String message) {
		super(message);
	}

	public DataException(Throwable cause) {
		super(cause);
	}

	public DataException(ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}

	public ResultStatus getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}

}
