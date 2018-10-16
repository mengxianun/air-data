package com.github.mengxianun.core;

import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import com.github.mengxianun.core.exception.DataException;
import com.github.mengxianun.core.exception.JsonDataException;
import com.github.mengxianun.core.item.ColumnItem;
import com.github.mengxianun.core.item.FilterItem;
import com.github.mengxianun.core.item.GroupItem;
import com.github.mengxianun.core.item.JoinItem;
import com.github.mengxianun.core.item.LimitItem;
import com.github.mengxianun.core.item.OrderItem;
import com.github.mengxianun.core.item.TableItem;
import com.github.mengxianun.core.item.ValueItem;
import com.github.mengxianun.core.json.Connector;
import com.github.mengxianun.core.json.JsonAttributes;
import com.github.mengxianun.core.json.Operation;
import com.github.mengxianun.core.json.Operator;
import com.github.mengxianun.core.json.Order;
import com.github.mengxianun.core.schema.Column;
import com.github.mengxianun.core.schema.Table;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonParser {

	private final DataTranslator translator;

	private DataContext dataContext;

	private final JsonObject jsonData;

	private Operation operation;
	private String operationAttribute;

	private Action action = new Action();
	private String nativeContent;

	public JsonParser(JsonObject jsonData, DataTranslator translator) {
		this.jsonData = jsonData;
		this.translator = translator;
		parseOperation();
	}

	private void parseOperation() {
		int operationCount = 0;
		if (jsonData.has(JsonAttributes.DETAIL)) {
			operation = Operation.DETAIL;
			operationAttribute = JsonAttributes.DETAIL;
			operationCount++;
		}
		if (jsonData.has(JsonAttributes.QUERY)) {
			operation = Operation.QUERY;
			operationAttribute = JsonAttributes.QUERY;
			operationCount++;
		}
		if (jsonData.has(JsonAttributes.SELECT)) {
			operation = Operation.SELECT;
			operationAttribute = JsonAttributes.SELECT;
			operationCount++;
		}
		if (jsonData.has(JsonAttributes.INSERT)) {
			operation = Operation.INSERT;
			operationAttribute = JsonAttributes.INSERT;
			operationCount++;
		}
		if (jsonData.has(JsonAttributes.UPDATE)) {
			operation = Operation.UPDATE;
			operationAttribute = JsonAttributes.UPDATE;
			operationCount++;
		}
		if (jsonData.has(JsonAttributes.DELETE)) {
			operation = Operation.DELETE;
			operationAttribute = JsonAttributes.DELETE;
			operationCount++;
		}
		if (jsonData.has(JsonAttributes.TRANSACTION)) {
			operation = Operation.TRANSACTION;
			operationAttribute = JsonAttributes.TRANSACTION;
			operationCount++;
		}
		if (jsonData.has(JsonAttributes.STRUCT)) {
			operation = Operation.STRUCT;
			operationAttribute = JsonAttributes.STRUCT;
			operationCount++;
		}
		if (jsonData.has(JsonAttributes.NATIVE)) {
			operation = Operation.NATIVE;
			operationAttribute = JsonAttributes.NATIVE;
			operationCount++;
		}

		if (operationCount > 1) {
			throw new JsonDataException("Multiple operations were found in the Json data.");
		} else if (operationCount < 1) {
			throw new JsonDataException("No operations were found in the Json data.");
		}

	}

	public Action parse() {
		parseTables();

		switch (operation) {
		case DETAIL:
		case QUERY:
		case SELECT:
			parseSelect();
			break;

		case INSERT:
			parseInsert();
			break;

		case UPDATE:
			parseUpdate();
			break;

		case DELETE:
			parseDelete();
			break;

		case NATIVE:
			parseNative();
			break;

		default:
			break;
		}

		action.setOperation(operation);
		action.setDataContext(dataContext);
		return action;
	}

	public void parseSelect() {
		parseJoins();
		parseColumns();
		parseWhere();
		parseGroups();
		parseOrders();
		parseLimit();
	}

	public void parseInsert() {
		parseValues();
	}

	public void parseUpdate() {
		parseValues();
		parseWhere();
	}

	public void parseDelete() {
		parseWhere();
	}

	/**
	 * 只解析数据源, 暂时只考虑一个数据源的情况
	 * 
	 * @return 数据源名称, 可能为空
	 */
	@Deprecated
	public String parseSource() {
		JsonElement tablesElement = jsonData.get(operationAttribute);
		JsonElement tableElement;
		if (tablesElement.isJsonArray()) {
			JsonArray tableArray = (JsonArray) tablesElement;
			tableElement = tableArray.get(0);
		} else {
			tableElement = tablesElement;
		}
		String tableString = tableElement.getAsString().trim();
		if (tableString.contains(JsonAttributes.COLUMN_ALIAS_KEY)) {
			String[] tableAlias = tableString.split(JsonAttributes.COLUMN_ALIAS_KEY);
			tableString = tableAlias[0];
		}
		if (tableString.contains(".")) {
			String[] tableSchema = tableString.split("\\.");
			return tableSchema[0];
		} else {
			return null;
		}
	}

	/**
	 * 解析 table 节点, 可以是数组或字符串
	 */
	public void parseTables() {
		JsonElement tablesElement = jsonData.get(operationAttribute);
		if (tablesElement.isJsonObject()) {
			throw new JsonDataException("table node cannot be an object");
		} else if (tablesElement.isJsonArray()) {
			JsonArray tableArray = (JsonArray) tablesElement;
			tableArray.forEach(e -> action.addTableItem(parseTable(e)));
		} else {
			action.addTableItem(parseTable(tablesElement));
		}
	}

	/**
	 * 解析 table 元素
	 * 
	 * @param tableElement
	 * @return
	 */
	private TableItem parseTable(JsonElement tableElement) {
		String tableString = tableElement.getAsString().trim();
		String alias = null;
		if (tableString.contains(JsonAttributes.COLUMN_ALIAS_KEY)) {
			String[] tableAlias = tableString.split(JsonAttributes.COLUMN_ALIAS_KEY);
			tableString = tableAlias[0];
			alias = tableAlias[1];
		}
		String sourceName;
		String tableName;
		if (tableString.contains(".")) {
			String[] tableSchema = tableString.split("\\.");
			sourceName = tableSchema[0];
			tableName = tableSchema[1];
		} else {
			sourceName = translator.getDefaultDataSource();
			tableName = tableString;
		}
		dataContext = translator.getDataContext(sourceName);
		Table table = dataContext.getTable(tableName);
		if (table == null) {
			throw new DataException(String.format("table [%s] does not exist", tableName));
		}
		return new TableItem(table, alias);
	}

	public void parseJoins() {

	}

	public JoinItem parseJoin() {
		return null;
	}

	/**
	 * 解析 fields 节点, 可以是数组或字符串
	 */
	public void parseColumns() {
		if (!validAttribute(JsonAttributes.FIELDS)) {
			return;
		}
		JsonElement columnsElement = jsonData.get(JsonAttributes.FIELDS);
		if (columnsElement.isJsonObject()) {
			throw new JsonDataException("fields node cannot be an object");
		} else if (columnsElement.isJsonArray()) {
			((JsonArray) columnsElement).forEach(e -> action.addColumnItem(this.parseColumn(e)));
		} else {
			action.addColumnItem(this.parseColumn(columnsElement));
		}
	}

	/**
	 * 解析 fields 元素
	 * 
	 * @param columnElement
	 * @return
	 */
	private ColumnItem parseColumn(JsonElement columnElement) {
		String columnString = columnElement.getAsString().trim();
		String alias = null;
		if (columnString.contains(JsonAttributes.COLUMN_ALIAS_KEY)) {
			String[] columnAlias = columnString.split(JsonAttributes.COLUMN_ALIAS_KEY);
			columnString = columnAlias[0];
			alias = columnAlias[1];

		}
		Column column = findColumn(columnString);
		if (column == null) {
			return new ColumnItem(columnString, alias);
		} else {
			return new ColumnItem(column, alias);
		}
	}

	/**
	 * 解析 where 节点, 可以是数组, 对象, 或字符串
	 */
	public void parseWhere() {
		if (!validAttribute(JsonAttributes.WHERE)) {
			return;
		}
		JsonElement whereElement = jsonData.get(JsonAttributes.WHERE);
		if (whereElement.isJsonArray()) {
			((JsonArray) whereElement).forEach(f -> action.addFilterItem(parseFilter(f)));
		} else if (whereElement.isJsonObject()) {
			Entry<String, JsonElement> objectFilter = ((JsonObject) whereElement).entrySet().iterator().next();
			String connectorString = objectFilter.getKey();
			JsonElement objectInnerFilter = objectFilter.getValue();
			FilterItem parsedFilter = parseFilter(objectInnerFilter);
			parsedFilter.setConnector(Connector.from(connectorString));
			action.addFilterItem(parsedFilter);
		} else {
			action.addFilterItem(parseFilter(whereElement));
		}
	}

	/**
	 * 解析 where 单个条件对象. 可以是数组,对象,字符串.
	 * 
	 * @param filterElement
	 * @return
	 */
	private FilterItem parseFilter(JsonElement filterElement) {
		FilterItem filterItem = new FilterItem();
		if (filterElement.isJsonArray()) {
			for (JsonElement innerFilterItem : (JsonArray) filterElement) {
				filterItem.addSubFilterItem(parseFilter(innerFilterItem));
			}
			return filterItem;
		} else if (filterElement.isJsonObject()) {
			Entry<String, JsonElement> objectFilter = ((JsonObject) filterElement).entrySet().iterator().next();
			String connectorString = objectFilter.getKey();
			JsonElement objectInnerFilter = objectFilter.getValue();
			if (objectInnerFilter.isJsonArray()) {
				for (JsonElement innerFilterItem : (JsonArray) objectInnerFilter) {
					filterItem.addSubFilterItem(parseFilter(innerFilterItem));
				}
			} else if (objectInnerFilter.isJsonPrimitive()) {
				filterItem = parseFilter(objectInnerFilter);
			} else {
				throw new JsonDataException("where node format error");
			}
			filterItem.setConnector(Connector.from(connectorString));
			return filterItem;
		} else {
			String filterString = filterElement.getAsString().trim();
			return parseFilter(filterString);
		}
	}

	/**
	 * 解析 where 字符串形式的条件. 如 "a=1"
	 * 
	 * @param filterString
	 * @return
	 */
	private FilterItem parseFilter(String filterString) {
		Cond cond = parseCond(filterString);
		Operator operator = cond.getOperator();
		String columnString = cond.getColumn();
		Object value = cond.getValue();

		Column column = findColumn(columnString);
		if (column == null) {
			return new FilterItem(columnString, value, Connector.AND, operator);
		} else {
			return new FilterItem(column, value, Connector.AND, operator);
		}
	}

	/**
	 * 解析条件运算符
	 * 
	 * @param quote
	 * @return
	 */
	private Cond parseCond(String filterString) {
		Operator operator = null;
		int pos = 0;
		int length = filterString.length();
		over: while (pos < length) {
			switch (filterString.charAt(pos++)) {
			case '=':
				String tail = filterString.substring(pos);
				if (tail.contains(",")) { // in
					operator = Operator.IN;
				} else if (tail.contains("~")) { // between
					operator = Operator.BETWEEN;
				} else { // 等于
					operator = Operator.EQUAL;
				}
				break over;
			case '!':
				switch (filterString.charAt(pos++)) {
				case '=':
					String notTail = filterString.substring(pos);
					if (notTail.contains(",")) { // in
						operator = Operator.NOT_IN;
					} else { // 等于
						operator = Operator.NOT_EQUAL;
					}
					break over;

				case '%':
					switch (filterString.charAt(pos++)) {
					case '=':
						operator = Operator.NOT_LIKE;
						break over;
					default:
						break;
					}
					break over;

				default:
					break;
				}
				break;
			case '>':
				switch (filterString.charAt(pos++)) {
				case '=':
					operator = Operator.GTE;
					break over;
				default:
					operator = Operator.GT;
				}
				break;
			case '<':
				switch (filterString.charAt(pos++)) {
				case '=':
					operator = Operator.LTE;
					break over;
				default:
					operator = Operator.LT;
				}
				break;
			case '%':
				switch (filterString.charAt(pos++)) {
				case '=':
					operator = Operator.LIKE;
					break over;
				default:
					break;
				}
				break;

			default:
				break;
			}
		}
		if (operator == null) {
			return null;
		}

		String[] kv = null;
		/*
		 * 对 in 和 between 做特殊处理, 待优化
		 */
		switch (operator) {
		case IN:
		case BETWEEN:
			kv = filterString.split(Operator.EQUAL.op(), 2);
			break;
		case NOT_IN:
			kv = filterString.split(Operator.NOT_EQUAL.op(), 2);
			break;

		default:
			kv = filterString.split(operator.op(), 2);
			break;
		}
		
		String column = kv[0];
		Object value = kv[1];
		// 多值条件时, 将多值字符串转换为数组
		if (operator == Operator.IN || operator == Operator.NOT_IN || operator == Operator.BETWEEN) {
			value = value.toString().split(operator.op());
		}

		return new Cond(operator, column, value);
	}

	private class Cond {

		private Operator operator;

		String column;

		Object value;

		public Cond(Operator operator, String column, Object value) {
			this.operator = operator;
			this.column = column;
			this.value = value;
		}

		public Operator getOperator() {
			return operator;
		}

		public String getColumn() {
			return column;
		}

		public Object getValue() {
			return value;
		}

	}

	/**
	 * 解析 Group 节点, 可以是数组或字符串
	 */
	public void parseGroups() {
		if (!validAttribute(JsonAttributes.GROUP)) {
			return;
		}
		JsonElement groupsElement = jsonData.get(JsonAttributes.GROUP);
		if (groupsElement.isJsonObject()) {
			throw new JsonDataException("group node cannot be an object");
		} else if (groupsElement.isJsonArray()) {
			((JsonArray) groupsElement).forEach(e -> action.addGroupItem(this.parseGroup(e)));
		} else {
			action.addGroupItem(this.parseGroup(groupsElement));
		}
	}

	/**
	 * 解析 Group 元素
	 * 
	 * @param groupElement
	 * @return
	 */
	public GroupItem parseGroup(JsonElement groupElement) {
		String groupString = groupElement.getAsString().trim();
		Column column = findColumn(groupString);
		if (column == null) {
			return new GroupItem(groupString);
		} else {
			return new GroupItem(column);
		}
	}

	/**
	 * 解析 Order 节点, 可以是数组或字符串
	 */
	public void parseOrders() {
		if (!validAttribute(JsonAttributes.ORDER)) {
			return;
		}
		JsonElement ordersElement = jsonData.get(JsonAttributes.ORDER);
		if (ordersElement.isJsonObject()) {
			throw new JsonDataException("order node cannot be an object");
		} else if (ordersElement.isJsonArray()) {
			((JsonArray) ordersElement).forEach(e -> action.addOrderItem(this.parseOrder(e)));
		} else {
			action.addOrderItem(this.parseOrder(ordersElement));
		}
	}

	/**
	 * 解析 Order 元素, 分为2种格式：1. "column desc", 2. "+column". 默认升序
	 * 
	 * @param orderElement
	 * @return
	 */
	public OrderItem parseOrder(JsonElement orderElement) {
		String orderString = orderElement.getAsString().trim();
		String columnString = null;
		Order order = null;
		if (orderString.endsWith(" asc") || orderString.endsWith(" desc")) {
			String[] columnOrder = orderString.split("\\s+");
			columnString = columnOrder[0];
			String orderTypeString = columnOrder[1];
			order = orderTypeString.equals("desc") ? Order.DESC : Order.ASC;
		} else if (orderString.startsWith("+") || orderString.startsWith("-")) {
			String symbol = orderString.substring(0, 1);
			columnString = orderString.substring(1);
			order = symbol.equals("-") ? Order.DESC : Order.ASC;
		} else {
			columnString = orderString;
			order = Order.ASC;
		}
		Column column = findColumn(columnString);
		if (column == null) {
			return new OrderItem(columnString, order);
		} else {
			return new OrderItem(column, order);
		}
	}

	public void parseLimit() {
		if (!validAttribute(JsonAttributes.LIMIT)) {
			return;
		}
		JsonElement limitElement = jsonData.get(JsonAttributes.LIMIT);
		if (!limitElement.isJsonArray()) {
			throw new JsonDataException("limit node must be an array");
		} else {
			JsonArray limitArray = (JsonArray) limitElement;
			long start = limitArray.get(0).getAsLong();
			long end = limitArray.get(1).getAsLong();
			action.addLimitItem(new LimitItem(start, end));
		}
	}

	public void parseValues() {
		if (!validAttribute(JsonAttributes.VALUES)) {
			return;
		}
		JsonElement valuesElement = jsonData.get(JsonAttributes.VALUES);
		if (!valuesElement.isJsonObject()) {
			throw new JsonDataException("values node must be an object");
		} else {
			JsonObject values = (JsonObject) valuesElement;
			for (String columnName : values.keySet()) {
				String value = values.getAsString();
				Column column = findColumn(columnName);
				if (column != null) {
					action.addValueItem(new ValueItem(column, value));
				}
			}
		}
	}

	/**
	 * 根据字符串查找字段
	 * 
	 * @param columnString
	 *            字段字符串
	 * @return
	 */
	private Column findColumn(String columnString) {
		long dotCount = columnString.chars().filter(ch -> ch == '.').count();
		String[] columnParts = columnString.split("\\.");
		if (dotCount == 2) { // schema.table.column
			String schemaName = columnParts[0];
			String tableName = columnParts[1];
			String columnName = columnParts[2];
			return dataContext.getColumn(schemaName, tableName, columnName);
		} else if (dotCount == 1) { // table.column
			String tableName = columnParts[0];
			String columnName = columnParts[1];
			return dataContext.getColumn(tableName, columnName);
		} else { // column
			// 在操作表中查找字段信息
			List<TableItem> tableItems = action.getTableItems();
			for (TableItem tableItem : tableItems) {
				List<Column> columns = tableItem.getTable().getColumns();
				Optional<Column> optionalColumn = columns.stream().filter(c -> c.getName().equals(columnString))
						.findFirst();
				if (optionalColumn.isPresent()) {
					return optionalColumn.get();
				}
			}
			return null;
		}
	}

	public void parseNative() {
		if (!validAttribute(JsonAttributes.NATIVE)) {
			return;
		}
		JsonElement nativeElement = jsonData.get(JsonAttributes.NATIVE);
		nativeContent = nativeElement.getAsString();
	}

	private boolean validAttribute(String attribute) {
		if (jsonData.has(attribute)) {
			JsonElement attributeElement = jsonData.get(attribute);
			if (attributeElement.isJsonNull()) {
				throw new JsonDataException(attribute + " node value is null");
			}
			return true;
		}
		return false;
	}

	public boolean isSelect() {
		return operation != null && (operation == Operation.QUERY || operation == Operation.SELECT);
	}

	public boolean isUpdate() {
		return operation != null && operation == Operation.UPDATE;
	}

	public boolean isInsert() {
		return operation != null && operation == Operation.INSERT;
	}

	public boolean isDelete() {
		return operation != null && operation == Operation.DELETE;
	}

	public boolean isTransaction() {
		return operation != null && operation == Operation.TRANSACTION;
	}

	public boolean isStruct() {
		return operation != null && operation == Operation.STRUCT;
	}

	public boolean isNative() {
		return operation != null && operation == Operation.NATIVE;
	}

	public DataContext getDataContext() {
		return dataContext;
	}

	public Operation getOperation() {
		return operation;
	}

	public Action getAction() {
		return action;
	}

	public String getNativeContent() {
		return nativeContent;
	}

}
