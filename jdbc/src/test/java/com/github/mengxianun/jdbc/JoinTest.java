package com.github.mengxianun.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.mengxianun.core.DataResultSet;
import com.google.gson.JsonArray;

@DisplayName("Jdbc join test")
public class JoinTest extends TestSupport {

	private static final String JSON_PARENT_PATH = "json/join/";

	@Test
	void testJoin() {
		DataResultSet dataResultSet = run(JSON_PARENT_PATH + "join.json");
		JsonArray result = (JsonArray) dataResultSet.getJsonData();
		assertTrue(result.size() > 0);
	}

	@Test
	void testJoinLimit() {
		DataResultSet dataResultSet = run(JSON_PARENT_PATH + "join_limit.json");
		JsonArray result = (JsonArray) dataResultSet.getJsonData();
		assertEquals(result.size(), 2);
	}

}
