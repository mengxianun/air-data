package com.github.mengxianun.jdbc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.mengxianun.core.DataResultSet;
import com.google.gson.JsonArray;

@DisplayName("Jdbc select test")
public class SelectTest extends TestSupport {

	private static final String JSON_PARENT_PATH = "json/select/";

	@Test
	void testSelectTable() {
		DataResultSet dataResultSet = run(JSON_PARENT_PATH + "select_table.json");
		JsonArray result = (JsonArray) dataResultSet.getData();
		assertTrue(result.size() > 0);
	}

	@Test
	void testSelectSourceTable() {
		DataResultSet dataResultSet = run(JSON_PARENT_PATH + "select_source_table.json");
		JsonArray result = (JsonArray) dataResultSet.getData();
		assertTrue(result.size() > 0);
	}

}
