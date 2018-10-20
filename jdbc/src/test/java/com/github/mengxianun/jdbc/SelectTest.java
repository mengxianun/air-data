package com.github.mengxianun.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.mengxianun.core.DataResultSet;
import com.google.gson.JsonArray;

@DisplayName("Jdbc select test")
public class SelectTest extends TestSupport {

	@Test
	void testSelectTable() {
		String json = readJson("json/select_table.json");
		DataResultSet dataResultSet = translator.translate(json);
		JsonArray result = (JsonArray) dataResultSet.getData();
		assertTrue(result.size() > 0);
	}

	@Test
	void testSelectSourceTable() {
		String json = readJson("json/select_source_table.json");
		DataResultSet dataResultSet = translator.translate(json);
		JsonArray result = (JsonArray) dataResultSet.getData();
		assertTrue(result.size() > 0);
	}

	@Test
	void testSelectJoin() {
		String json = readJson("json/select_join.json");
		DataResultSet dataResultSet = translator.translate(json);
		JsonArray result = (JsonArray) dataResultSet.getData();
		// System.out.println(result.toString());
		assertTrue(result.size() > 0);
	}

	@Test
	void testSelectJoinLimit() {
		String json = readJson("json/select_join_limit.json");
		DataResultSet dataResultSet = translator.translate(json);
		JsonArray result = (JsonArray) dataResultSet.getData();
		assertEquals(result.size(), 2);
	}

	@Test
	@Disabled("for demonstration purposes")
	void skippedTest() {
		// not executed
	}

}
