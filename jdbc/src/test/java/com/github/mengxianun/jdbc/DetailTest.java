package com.github.mengxianun.jdbc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.mengxianun.core.DataResultSet;
import com.google.gson.JsonObject;

@DisplayName("Jdbc detail test")
public class DetailTest extends TestSupport {

	private static final String JSON_PARENT_PATH = "json/detail/";

	@Test
	void testDetailTable() {
		DataResultSet dataResultSet = run(JSON_PARENT_PATH + "detail.json");
		Object data = dataResultSet.getJsonData();
		assertTrue(data instanceof JsonObject);
	}

}
