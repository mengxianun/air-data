package com.github.mengxianun.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.github.mengxianun.core.item.ColumnItem;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

public abstract class AbstractDataResultSet implements DataResultSet {

	protected long took;

	protected List<ColumnItem> columnItems;

	protected Object data;

	protected ResultStatus resultStatus;

	public AbstractDataResultSet() {

	}

	public AbstractDataResultSet(long took, Object data) {
		this.took = took;
		this.data = data;
		this.resultStatus = ResultStatus.SUCCESS;
	}

	public AbstractDataResultSet(ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
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
		return resultStatus.code();
	}

	public String getMessage() {
		return resultStatus.message();
	}

}
