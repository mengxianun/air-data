package com.github.mengxianun.core;

import java.util.List;

import com.github.mengxianun.core.attributes.AssociationType;
import com.github.mengxianun.core.item.ColumnItem;
import com.github.mengxianun.core.item.JoinColumnItem;
import com.github.mengxianun.core.item.JoinItem;
import com.github.mengxianun.core.schema.Column;
import com.github.mengxianun.core.schema.Table;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * 结果数据渲染器
 * 
 * @author mengxiangyun
 *
 */
public class DataRenderer {

	/**
	 * 根据数据类型渲染数据.
	 * 
	 * @param data
	 * @param action
	 * @return
	 */
	public JsonElement render(JsonElement data, Action action) {
		if (data.isJsonArray()) {
			return render(data.getAsJsonArray(), action);
		} else if (data.isJsonObject()) {
			return render(data.getAsJsonObject(), action);
		} else {
			return data;
		}
	}

	public JsonElement render(JsonArray data, Action action) {
		// 主表唯一记录对象, key 为主表所有列的的值拼接的字符串, value 为主表唯一记录的对象
		JsonObject uniqueRecords = new JsonObject();
		for (JsonElement jsonElement : data) {
			JsonObject record = jsonElement.getAsJsonObject();
			// 主表的唯一记录对象
			JsonObject uniqueRecord;
			// 主表的唯一记录标识
			String uniqueRecordKey = createUniqueRecordKey(record, action);
			if (uniqueRecords.has(uniqueRecordKey)) {
				uniqueRecord = uniqueRecords.getAsJsonObject(uniqueRecordKey);
			} else {
				uniqueRecord = new JsonObject();
				uniqueRecords.add(uniqueRecordKey, uniqueRecord);
			}
			List<JoinItem> joinItems = action.getJoinItems();
			// 构建关联信息
			if (!joinItems.isEmpty() && action.getGroupItems().isEmpty()) {
				// 出现过的 join 表的对象, 用于 join 表的列再次获取已经创建的 join 表对象
				JsonObject existJoinTables = new JsonObject();
				List<ColumnItem> columnItems = action.getColumnItems();
				// 当前循环列的表数据对象
				JsonObject currentTableObject = uniqueRecord;
				for (ColumnItem columnItem : columnItems) {
					if (columnItem instanceof JoinColumnItem) {
						JoinColumnItem joinColumnItem = (JoinColumnItem) columnItem;
						Table joinTable = joinColumnItem.getTableItem().getTable();
						if (existJoinTables.has(joinTable.getName())) {
							JsonObject joinTableObject = existJoinTables.getAsJsonObject(joinTable.getName());
							JsonPrimitive value = record.getAsJsonPrimitive(columnItem.getAlias());
							addColumnValue(joinTableObject, columnItem, value);
							continue;
						}
						List<Table> parentTables = joinColumnItem.getParentTables();
						// -- 构建 join 表上层结构
						for (int i = 0; i < parentTables.size() - 1; i++) {
							// 父级表, 第一个元素是主表, 跳过
							Table parentTable = parentTables.get(i + 1);
							// 已经构建了该 join 表的结构, 直接获取
							if (uniqueRecord.has(parentTable.getName())) {
								JsonElement parentElement = uniqueRecord.get(parentTable.getName());
								if (parentElement.isJsonArray()) {
									JsonArray parentArray = parentElement.getAsJsonArray();
									// 获取数组关联表的最新的元素, 即当前正在循环的元素
									currentTableObject = parentArray.get(parentArray.size() - 1).getAsJsonObject();
								} else {
									currentTableObject = parentElement.getAsJsonObject();
								}
							} else {
								currentTableObject = createJoinStructure(currentTableObject, parentTables.get(i),
										parentTables.get(i + 1));
							}
						}
						// -- 构建 join 表结构
						// join 表的父级表
						Table parentTable = parentTables.get(parentTables.size() - 1);
						currentTableObject = createJoinStructure(currentTableObject, parentTable, joinTable);
						// 记录出现过的 join 表
						existJoinTables.add(joinTable.getName(), currentTableObject);

						JsonPrimitive value = record.getAsJsonPrimitive(columnItem.getAlias());
						addColumnValue(currentTableObject, columnItem, value);
					} else {
						JsonPrimitive value = record.getAsJsonPrimitive(columnItem.getAlias());
						addColumnValue(currentTableObject, columnItem, value);
					}
				}
			} else {
				List<ColumnItem> columnItems = action.getColumnItems();
				for (ColumnItem columnItem : columnItems) {
					JsonPrimitive value = record.getAsJsonPrimitive(columnItem.getAlias());
					addColumnValue(uniqueRecord, columnItem, value);
				}
			}
		}
		JsonArray renderData = new JsonArray();
		for (String uniqueKey : uniqueRecords.keySet()) {
			renderData.add(uniqueRecords.getAsJsonObject(uniqueKey));
		}
		return renderData;

	}

	/**
	 * 生成主表每条记录的唯一标识
	 * 
	 * @param record
	 * @param action
	 * @return
	 */
	public String createUniqueRecordKey(JsonObject record, Action action) {
		StringBuilder uniqueKey = new StringBuilder();
		List<ColumnItem> columnItems = action.getColumnItems();
		for (ColumnItem columnItem : columnItems) {
			if (!(columnItem instanceof JoinColumnItem)) { // 主表列
				// Column column = columnItem.getColumn();
				String columnAlias = columnItem.getAlias();
				Object value = null;
				if (record.has(columnAlias)) {
					value = record.get(columnAlias);
				} else if (record.has(columnAlias.toUpperCase())) {
					record.get(columnAlias.toUpperCase());
				} else if (record.has(columnAlias.toLowerCase())) {
					record.get(columnAlias.toLowerCase());
				}
				uniqueKey.append(value.toString());
			}
		}
		return uniqueKey.toString();
	}

	public JsonElement render(JsonObject data, Action action) {
		JsonObject jsonData = new JsonObject();
		List<JoinItem> joinItems = action.getJoinItems();
		// 构建关联信息
		if (!joinItems.isEmpty() && action.getGroupItems().isEmpty()) {
			// 出现过的 join 表的对象, 用于 join 表的列再次获取已经创建的 join 表对象
			JsonObject existJoinTables = new JsonObject();
			List<ColumnItem> columnItems = action.getColumnItems();
			// 当前循环列的表数据对象
			JsonObject currentTableObject = jsonData;
			for (ColumnItem columnItem : columnItems) {
				if (columnItem instanceof JoinColumnItem) {
					JoinColumnItem joinColumnItem = (JoinColumnItem) columnItem;
					Table joinTable = joinColumnItem.getTableItem().getTable();
					if (existJoinTables.has(joinTable.getName())) {
						JsonObject joinTableObject = existJoinTables.getAsJsonObject(joinTable.getName());
						JsonPrimitive value = data.getAsJsonPrimitive(columnItem.getAlias());
						addColumnValue(joinTableObject, columnItem, value);
						continue;
					}
					List<Table> parentTables = joinColumnItem.getParentTables();
					// -- 构建 join 表上层结构
					for (int i = 0; i < parentTables.size() - 1; i++) {
						currentTableObject = createJoinStructure(currentTableObject, parentTables.get(i),
								parentTables.get(i + 1));
					}
					// -- 构建 join 表结构
					// join 表的父级表
					Table parentTable = parentTables.get(parentTables.size() - 1);
					currentTableObject = createJoinStructure(currentTableObject, parentTable, joinTable);
					// 记录出现过的 join 表
					existJoinTables.add(joinTable.getName(), currentTableObject);

					JsonPrimitive value = data.getAsJsonPrimitive(columnItem.getAlias());
					addColumnValue(currentTableObject, columnItem, value);
				} else {
					JsonPrimitive value = data.getAsJsonPrimitive(columnItem.getAlias());
					addColumnValue(jsonData, columnItem, value);
				}
			}
			return jsonData;
		}
		List<ColumnItem> columnItems = action.getColumnItems();
		if (columnItems.isEmpty()) {
			return new Gson().toJsonTree(data);
		} else {
			for (ColumnItem columnItem : columnItems) {
				JsonPrimitive value = data.getAsJsonPrimitive(columnItem.getAlias());
				addColumnValue(jsonData, columnItem, value);
			}
		}
		return jsonData;
	}

	public void addColumnValue(JsonObject record, ColumnItem columnItem, JsonPrimitive value) {
		Column column = columnItem.getColumn();
		String columnAlias = columnItem.getAlias();
		String columnKey = column == null ? columnAlias : treatColumn(column.getName());
		record.addProperty(columnKey, render(column, value));
	}

	/**
	 * 处理返回的列名, 变成小写
	 * 
	 * @param columnName
	 * @return
	 */
	public String treatColumn(String columnName) {
		return columnName.toLowerCase();
	}

	public String render(Column column, JsonPrimitive value) {
		if (value == null) {
			return null;
		}
		//////////////////
		// JsonObject columnConfig = column.getConfig();
		// to do
		//////////////////
		return value.getAsString();
	}

	public JsonObject createJoinStructure(JsonObject currentTableObject, Table parentTable, Table joinTable) {
		return createJoinStructure(currentTableObject, joinTable.getName(),
				parentTable.getAssociationType(joinTable));
	}

	public JsonObject createJoinStructure(JsonObject currentTableObject, String tableName,
			AssociationType associationType) {
		switch (associationType) {
		case ONE_TO_ONE:
		case MANY_TO_ONE:
			currentTableObject = createJoinObject(currentTableObject, tableName);
			break;
		case ONE_TO_MANY:
		case MANY_TO_MANY:
			currentTableObject = createJoinArray(currentTableObject, tableName);
			break;

		default:
			break;
		}
		return currentTableObject;
	}

	public JsonObject createJoinObject(JsonObject currentTableObject, String tableName) {
		if (currentTableObject.has(tableName)) {
			currentTableObject = currentTableObject.getAsJsonObject(tableName);
		} else {
			JsonObject tempJsonObject = new JsonObject();
			currentTableObject.add(tableName, tempJsonObject);
			currentTableObject = tempJsonObject;
		}
		return currentTableObject;
	}

	public JsonObject createJoinArray(JsonObject currentTableObject, String tableName) {
		if (currentTableObject.has(tableName)) {
			JsonArray tempJsonArray = currentTableObject.getAsJsonArray(tableName);
			JsonObject tempJsonObject = new JsonObject();
			tempJsonArray.add(tempJsonObject);
			currentTableObject = tempJsonObject;
		} else {
			JsonArray tempJsonArray = new JsonArray();
			JsonObject tempJsonObject = new JsonObject();
			tempJsonArray.add(tempJsonObject);
			currentTableObject.add(tableName, tempJsonArray);
			currentTableObject = tempJsonObject;
		}
		return currentTableObject;
	}

}
