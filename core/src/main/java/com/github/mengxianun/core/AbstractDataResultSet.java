package com.github.mengxianun.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

public abstract class AbstractDataResultSet implements DataResultSet {

	protected long took;

	protected Object data;

	protected int code;
	protected String message;

	public AbstractDataResultSet() {

	}

	public AbstractDataResultSet(long took, Object data) {
		this.took = took;
		this.data = data;
		this.code = ResultStatus.SUCCESS.code();
		this.message = ResultStatus.SUCCESS.message();
	}

	public AbstractDataResultSet(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public AbstractDataResultSet(ResultStatus resultStatus) {
		this.code = ResultStatus.SUCCESS.code();
		this.message = ResultStatus.SUCCESS.message();
	}

	@Override
	public InputStream getDataStream(ResultType resultType) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		CSVWriterBuilder csvWriterBuilder = new CSVWriterBuilder(
				new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8));
		try (ICSVWriter icsvWriter = csvWriterBuilder.build()) {
			// icsvWriter.writeNext(columnHeader.toArray(new String[] {}));
			// icsvWriter.writeNext(columnHeaderDisplay.toArray(new String[] {}));
		}
		return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
	}

	public long getTook() {
		return took;
	}

	public void setTook(long took) {
		this.took = took;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
