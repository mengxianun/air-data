package com.github.mengxianun.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.mengxianun.core.DataResultSet;
import com.github.mengxianun.core.attributes.ResultAttributes;
import com.google.gson.JsonObject;

@DisplayName("Jdbc delete test")
public class DeleteTest extends TestSupport {

	private static final String JSON_PARENT_PATH = "json/delete/";

	@Test
	void testDeleteTable() {
		DataResultSet dataResultSet = run(JSON_PARENT_PATH + "delete.json");
		JsonObject result = (JsonObject) dataResultSet.getJsonData();
		assertTrue(result.has(ResultAttributes.COUNT.toString().toLowerCase()));
		int count = result.getAsJsonPrimitive(ResultAttributes.COUNT.toString().toLowerCase()).getAsInt();
		assertEquals(count, 1);
	}

}
