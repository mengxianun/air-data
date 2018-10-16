package com.github.mengxianun.core;

import java.io.IOException;
import java.io.InputStream;

public interface DataResultSet {

	public long getTook();

	public Object getData();

	public int getCode();

	public String getMessage();

	// public List<Object[]> toObjectArrays();

	// public List<Map<String, Object>> toMapList();

	public String toString();

	// public InputStream toDataStream();

	public InputStream getDataStream(ResultType resultType) throws IOException;

}
