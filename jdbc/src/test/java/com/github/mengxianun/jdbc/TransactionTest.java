package com.github.mengxianun.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.mengxianun.core.DataResultSet;
import com.github.mengxianun.core.attributes.ResultAttributes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DisplayName("Jdbc transaction test")
public class TransactionTest extends TestSupport {

	private static final String JSON_PARENT_PATH = "json/transaction/";

	@Test
	void testTransaction1() {
		DataResultSet dataResultSet = run(JSON_PARENT_PATH + "transaction1.json");
		JsonArray transactionResult = (JsonArray) dataResultSet.getJsonData();
		assertEquals(transactionResult.size(), 2);
		for (JsonElement jsonElement : transactionResult) {
			JsonObject result = (JsonObject) jsonElement;
			assertTrue(result.has(ResultAttributes.COUNT.toString().toLowerCase()));
			int count = result.getAsJsonPrimitive(ResultAttributes.COUNT.toString().toLowerCase()).getAsInt();
			assertEquals(count, 1);
		}
		validTable1("new_name");
		validTable2("new_name");
	}

	@Test
	void testTransaction2() {
		DataResultSet dataResultSet = run(JSON_PARENT_PATH + "transaction2.json");
		JsonArray transactionResult = (JsonArray) dataResultSet.getJsonData();
		assertEquals(transactionResult.size(), 2);
		for (JsonElement jsonElement : transactionResult) {
			JsonObject result = (JsonObject) jsonElement;
			assertTrue(result.has(ResultAttributes.COUNT.toString().toLowerCase()));
			int count = result.getAsJsonPrimitive(ResultAttributes.COUNT.toString().toLowerCase()).getAsInt();
			assertEquals(count, 1);
		}
		validTable1("table1_name");
		validTable2("table2_name");
	}

	void validTable1(String name) {
		DataResultSet dataResultSet = run(JSON_PARENT_PATH + "select_table_1.json");
		JsonArray result = (JsonArray) dataResultSet.getJsonData();
		JsonObject jsonObject = (JsonObject) result.get(0);
		assertEquals(jsonObject.get("name").getAsString(), name);
	}

	void validTable2(String name) {
		DataResultSet dataResultSet = run(JSON_PARENT_PATH + "select_table_2.json");
		JsonArray result = (JsonArray) dataResultSet.getJsonData();
		JsonObject jsonObject = (JsonObject) result.get(0);
		assertEquals(jsonObject.get("name").getAsString(), name);
	}

}
