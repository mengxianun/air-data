package com.github.mengxianun.core.resutset;

import com.github.mengxianun.core.AbstractDataResultSet;
import com.github.mengxianun.core.ResultStatus;

public class DefaultDataResultSet extends AbstractDataResultSet {

	public DefaultDataResultSet() {
		super(0, null);
	}

	public DefaultDataResultSet(Object data) {
		super(0, data);
	}

	public DefaultDataResultSet(long took, Object data) {
		super(took, data);
	}

	public DefaultDataResultSet(ResultStatus resultStatus) {
		super(resultStatus);
	}

}
