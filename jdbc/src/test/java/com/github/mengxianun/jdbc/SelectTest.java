package com.github.mengxianun.jdbc;

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

@DisplayName("Jdbc Select test")
public class SelectTest extends TestSupport {

	@BeforeAll
	static void initAll() {
		try {
			Class.forName(DB_DRIVER_CLASS_NAME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		createDataContext();
		initDatabase();
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
	void testSelectSourceTable() {
		String json = readJson("json/select_source_table.json");
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
