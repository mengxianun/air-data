package com.github.mengxianun.elasticsearch;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.mengxianun.core.DataResultSet;

@DisplayName("Elasticsearch SQL test")
public class SQLTest extends TestSupport {

	@BeforeAll
	static void initAll() {
		createDataContext();
	}

	@BeforeEach
	void init() {
	}

	@Test
	void testSelectTable() {
		String json = readJson("json/select_table.json");
		DataResultSet dataResultSet = translator.translate(json);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> data = (List<Map<String, Object>>) dataResultSet.getData();
		assertTrue(data.size() > 0);
	}

	@Test
	@Disabled("for demonstration purposes")
	void skippedTest() {
		// not executed
	}

	@AfterEach
	void tearDown() {
	}

	@AfterAll
	static void tearDownAll() {
	}

}
