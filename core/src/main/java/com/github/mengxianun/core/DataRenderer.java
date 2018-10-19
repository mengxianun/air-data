package com.github.mengxianun.core;

import java.util.List;
import java.util.Map;

import com.github.mengxianun.core.attributes.AssociationType;
import com.github.mengxianun.core.item.ColumnItem;
import com.github.mengxianun.core.item.JoinColumnItem;
import com.github.mengxianun.core.item.JoinItem;
import com.github.mengxianun.core.schema.Column;
import com.github.mengxianun.core.schema.Table;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
	@SuppressWarnings("unchecked")
	public Object render(Object data, Action action) {
		if (data instanceof List) {
			return render((List<Map<String, Object>>) data, action);
		} else if (data instanceof Map) {
			return render((Map<String, Object>) data, action);
		}
		return data;
	}

	public JsonElement render(List<Map<String, Object>> data, Action action) {
		// List<Map<String, Object>> renderData = new ArrayList<>();


		// 主表唯一记录对象, key 为主表所有列的的值拼接的字符串, value 为主表唯一记录的对象
		JsonObject uniqueRecords = new JsonObject();
		for (Map<String, Object> record : data) {
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
							Column column = columnItem.getColumn();
							Object value = record.get(columnItem.getAlias());
							joinTableObject.addProperty(column.getName(), value == null ? "" : value.toString());
							continue;
						}
						List<Table> parentTables = joinColumnItem.getParentTables();
						// -- 构建 join 表上层结构
						for (int i = 0; i < parentTables.size() - 1; i++) {
							// 父级表, 第一个元素是主表, 跳过
							Table parentTable = parentTables.get(i + 1);
							// 已经构建了该 join 表的结构, 直接获取
							if (uniqueRecord.has(parentTable.getName())) {
								JsonElement jsonElement = uniqueRecord.get(parentTable.getName());
								if (jsonElement.isJsonArray()) {
									JsonArray jsonArray = jsonElement.getAsJsonArray();
									// 获取数组关联表的最新的元素, 即当前正在循环的元素
									currentTableObject = jsonArray.get(jsonArray.size() - 1).getAsJsonObject();
								} else {
									currentTableObject = jsonElement.getAsJsonObject();
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

						Column column = columnItem.getColumn();
						Object value = record.get(columnItem.getAlias());
						currentTableObject.addProperty(column.getName(), value == null ? "" : value.toString());
					} else {
						// jsonData = new JsonObject();
						Column column = columnItem.getColumn();
						Object value = record.get(columnItem.getAlias());

						currentTableObject.addProperty(column.getName(), value == null ? "" : value.toString());
					}
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
	 * @param data
	 * @param action
	 * @return
	 */
	public String createUniqueRecordKey(Map<String, Object> data, Action action) {
		StringBuilder uniqueKey = new StringBuilder();
		List<ColumnItem> columnItems = action.getColumnItems();
		for (ColumnItem columnItem : columnItems) {
			if (!(columnItem instanceof JoinColumnItem)) { // 主表列
				// Column column = columnItem.getColumn();
				Object value = data.get(columnItem.getAlias());
				uniqueKey.append(value == null ? "" : value.toString());
			}
		}
		return uniqueKey.toString();
	}

	public JsonElement render(Map<String, Object> data, Action action) {
		// JsonObject jsonData = null;
		// Map<String, Object> renderData = new LinkedHashMap<>();
		// Gson gson = new Gson();
		// Map<String, Object> renderData = gson.fromJson(jsonObject, new
		// TypeToken<LinkedHashMap<String, String>>() {
		// }.getType());
		JsonObject jsonData = new JsonObject();
		List<JoinItem> joinItems = action.getJoinItems();
		// 构建关联信息
		if (!joinItems.isEmpty() && action.getGroupItems().isEmpty()) {
			// 出现过的 join 表的对象, 用于 join 表的列再次获取已经创建的 join 表对象
			JsonObject existJoinTables = new JsonObject();
			// Json 格式记录
			List<ColumnItem> columnItems = action.getColumnItems();
			for (ColumnItem columnItem : columnItems) {
				if (columnItem instanceof JoinColumnItem) {
					JoinColumnItem joinColumnItem = (JoinColumnItem) columnItem;
					Table joinTable = joinColumnItem.getTableItem().getTable();
					if (existJoinTables.has(joinTable.getName())) {
						JsonObject joinTableObject = existJoinTables.getAsJsonObject(joinTable.getName());
						Column column = columnItem.getColumn();
						Object value = data.get(columnItem.getAlias());
						// renderData.put(column.getName(), value);
						joinTableObject.addProperty(column.getName(), value == null ? "" : value.toString());
					}
					// AssociationType associationType = joinColumnItem.getAssociationType();
					List<Table> parentTables = joinColumnItem.getParentTables();
					// 当前循环列的表数据对象
					JsonObject currentTableObject = jsonData;
					// -- 构建 join 父级表上层结构
					// i 从1开始, 因为第一个元素是主表, 不需要构建结构
					for (int i = 0; i < parentTables.size() - 1; i++) {
						currentTableObject = createJoinStructure(currentTableObject, parentTables.get(i),
								parentTables.get(i + 1));
						// Relationship relationship;
						// AssociationType associationType;
						// Table parentTable = parentTables.get(i);
						// if (i == parentTables.size() - 1) {
						// relationship = parentTables.get(i).getRelationship(joinTable);
						// if (relationship == null) {
						// relationship = joinTable.getRelationship(parentTables.get(i));
						// associationType = relationship.getAssociationType().reverse();
						// } else {
						// associationType = relationship.getAssociationType();
						// }
						// } else {
						// relationship = parentTables.get(i).getRelationship(parentTables.get(i + 1));
						// associationType = relationship.getAssociationType();
						// }
						// // AssociationType associationType = relationship.getAssociationType();
						// switch (associationType) {
						// case ONE_TO_ONE:
						// case MANY_TO_ONE:
						// if (!currentTableObject.has(parentTable.getName())) {
						// JsonObject tempJsonObject = new JsonObject();
						// currentTableObject.add(parentTable.getName(), tempJsonObject);
						// currentTableObject = tempJsonObject;
						// } else {
						// currentTableObject =
						// currentTableObject.getAsJsonObject(parentTable.getName());
						// }
						// break;
						// case ONE_TO_MANY:
						// case MANY_TO_MANY:
						// if (!jsonData.has(parentTable.getName())) {
						// JsonArray tempJsonArray = new JsonArray();
						// JsonObject tempJsonObject = new JsonObject();
						// tempJsonArray.add(tempJsonObject);
						// currentTableObject.add(parentTable.getName(), tempJsonArray);
						// currentTableObject = tempJsonObject;
						// } else {
						// JsonArray tempJsonArray =
						// currentTableObject.getAsJsonArray(parentTable.getName());
						// JsonObject tempJsonObject = new JsonObject();
						// tempJsonArray.add(tempJsonObject);
						// currentTableObject.add(parentTable.getName(), tempJsonArray);
						// currentTableObject = tempJsonObject;
						// }
						// break;
						//
						// default:
						// break;
						// }
					}
					// -- 构建 join 表结构
					// join 表的父级表
					Table parentTable = parentTables.get(parentTables.size() - 1);
					// if (parentTables.size() > 1) { // parentTables 第一个元素为主表
					// }
					currentTableObject = createJoinStructure(currentTableObject, parentTable, joinTable);
					// 记录出现过的 join 表
					existJoinTables.add(joinTable.getName(), currentTableObject);
					// -- 构建 join 表结构
					// JsonObject joinObject = new JsonObject();
					// currentTableObject.add(joinTable.getName(), joinObject);
					// currentTableObject = joinObject;
					// createJoinStructure(currentTableObject, parentTable, joinTable);

					Column column = columnItem.getColumn();
					Object value = data.get(columnItem.getAlias());
					// renderData.put(column.getName(), value);
					currentTableObject.addProperty(column.getName(), value == null ? "" : value.toString());
				} else {
					// jsonData = new JsonObject();
					Column column = columnItem.getColumn();
					Object value = data.get(columnItem.getAlias());
					// renderData.put(column.getName(), value);

					// resultStruct.addProperty(column.getName(), value == null ? "" :
					// value.toString());
					jsonData.addProperty(column.getName(), value == null ? "" : value.toString());
				}
			}
			// Gson gson = new Gson();
			// Map<String, Object> renderData = gson.fromJson(jsonData, new
			// TypeToken<LinkedHashMap<String, String>>() {
			// }.getType());
			return jsonData;
		}
		return jsonData;
	}

	public JsonObject createJoinStructure(JsonObject currentTableObject, Table parentTable, Table joinTable) {
		// AssociationType associationType = parentTable.getAssociationType(joinTable);
		// relationship = parentTable.getRelationship(joinTable);
		// if (relationship == null) {
		// relationship = joinTable.getRelationship(parentTable);
		// associationType = relationship.getAssociationType().reverse();
		// } else {
		// associationType = relationship.getAssociationType();
		// }
		// AssociationType associationType = relationship.getAssociationType();
		// switch (associationType) {
		// case ONE_TO_ONE:
		// case MANY_TO_ONE:
		// createJoinObject(currentTableObject, parentTable.getName());
		// break;
		// case ONE_TO_MANY:
		// case MANY_TO_MANY:
		// createJoinArray(currentTableObject, parentTable.getName());
		// break;
		//
		// default:
		// break;
		// }
		// createJoinStructure(currentTableObject, parentTable.getName(),
		// parentTable.getAssociationType(joinTable));
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
