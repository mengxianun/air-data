package com.github.mengxianun.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.mengxianun.core.DataResultSet;
import com.github.mengxianun.core.attributes.ResultAttributes;
import com.google.gson.JsonObject;

@DisplayName("Jdbc update test")
public class UpdateTest extends TestSupport {

	@Test
	void testUpdateTable() {
		String json = readJson("json/update_table.json");
		DataResultSet dataResultSet = translator.translate(json);
		JsonObject result = (JsonObject) dataResultSet.getData();
		assertTrue(result.has(ResultAttributes.COUNT.toString().toLowerCase()));
		int count = result.getAsJsonPrimitive(ResultAttributes.COUNT.toString().toLowerCase()).getAsInt();
		assertEquals(count, 1);
	}

}
