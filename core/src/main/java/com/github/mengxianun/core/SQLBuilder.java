package com.github.mengxianun.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.mengxianun.core.exception.DataException;
import com.github.mengxianun.core.item.ColumnItem;
import com.github.mengxianun.core.item.FilterItem;
import com.github.mengxianun.core.item.GroupItem;
import com.github.mengxianun.core.item.JoinItem;
import com.github.mengxianun.core.item.LimitItem;
import com.github.mengxianun.core.item.OrderItem;
import com.github.mengxianun.core.item.TableItem;
import com.github.mengxianun.core.item.ValueItem;
import com.github.mengxianun.core.json.Connector;
import com.github.mengxianun.core.json.Operator;
import com.github.mengxianun.core.schema.Column;
import com.github.mengxianun.core.schema.Schema;
import com.github.mengxianun.core.schema.Table;
import com.google.common.base.Strings;

public class SQLBuilder {

	public static final String PREFIX_SELECT = "SELECT ";
	public static final String PREFIX_FROM = " FROM ";
	public static final String PREFIX_WHERE = " WHERE ";
	public static final String PREFIX_GROUP_BY = " GROUP BY ";
	public static final String PREFIX_HAVING = " HAVING ";
	public static final String PREFIX_ORDER_BY = " ORDER BY ";
	public static final String PREFIX_LIMIT = " LIMIT ";
	public static final String ORDER_ASC = " ASC";
	public static final String ORDER_DESC = " DESC";
	public static final String INNER_JOIN = " INNER JOIN ";
	public static final String LEFT_OUTER_JOIN = " LEFT JOIN ";
	public static final String RIGHT_OUTER_JOIN = " RIGHT JOIN ";
	public static final String FULL_OUTER_JOIN = " FULL JOIN ";
	public static final String JOIN_ON = " ON ";
	public static final String DELIM_COMMA = ", ";
	public static final String DELIM_AND = " AND ";
	public static final String DELIM_OR = " OR ";

	public static final String COLUMN_ALL = "*";
	// 字段别名关联字符串
	public static final String ALIAS_KEY = " AS ";

	public static final String PREFIX_INSERT_INTO = "INSERT INTO ";
	public static final String PREFIX_UPDATE = "UPDATE ";
	public static final String UPDATE_SET = " SET ";
	public static final String PREFIX_DELETE_FROM = "DELETE FROM ";

	private Action action;

	private DataContext dataContext;

	private String sql;

	private List<Object> params = new ArrayList<>();

	private List<Object> whereParams = new ArrayList<>();

	private String countSql;

	public SQLBuilder(Action action) {
		this.action = action;
		this.dataContext = action.getDataContext();
		toSql();
	}

	public void toSql() {
		if (action.isDetail() || action.isSelect()) {
			toSelect();
		} else if (action.isInsert()) {
			toInsert();
		} else if (action.isUpdate()) {
			toUpdate();
		} else if (action.isDelete()) {
			toDelete();
		}
	}

	public void toSelect() {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append(toColumns());
		sqlBuilder.append(toTables());
		sqlBuilder.append(toJoins());
		sqlBuilder.append(toWhere());
		sqlBuilder.append(toGroups());
		sqlBuilder.append(toOrders());
		sqlBuilder.append(toLimit());
		sql = sqlBuilder.toString();
	}

	public void toInsert() {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append(toInsertTable());
		sqlBuilder.append(toInsertValues());
		sql = sqlBuilder.toString();
	}

	public void toUpdate() {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append(toUpdateTable());
		sqlBuilder.append(toUpdateValues());
		sqlBuilder.append(toWhere());
		sql = sqlBuilder.toString();
	}

	public void toDelete() {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append(toDeleteTable());
		sqlBuilder.append(toWhere());
		sql = sqlBuilder.toString();
	}

	public String toColumns() {
		StringBuilder columnsBuilder = new StringBuilder(PREFIX_SELECT);
		List<ColumnItem> columnItems = action.getColumnItems();
		if (columnItems.isEmpty()) {
			columnsBuilder.append(COLUMN_ALL).append(" ");
		} else {
			boolean comma = false;
			for (ColumnItem columnItem : columnItems) {
				if (comma) {
					columnsBuilder.append(", ");
				}
				Column column = columnItem.getColumn();
				if (column != null) {
					TableItem tableItem = columnItem.getTableItem();
					if (tableItem != null) { // 列所属表指定了别名
						String tableAlias = tableItem.getAlias();
						columnsBuilder.append(tableAlias);
					} else { // 列所属表没有指定别名, 使用真实表名作为前缀
						String tableName = column.getTable().getName();
						columnsBuilder.append(quote(tableName));
					}
					columnsBuilder.append(".").append(quote(column.getName()));
				} else {
					columnsBuilder.append(columnItem.getExpression());
				}
				String alias = columnItem.getAlias();
				if (!Strings.isNullOrEmpty(alias)) {
					columnsBuilder.append(ALIAS_KEY).append(alias);
				}
				comma = true;
			}
		}
		return columnsBuilder.toString();
	}

	public String toTables() {
		StringBuilder tablesBuilder = new StringBuilder(PREFIX_FROM);
		List<TableItem> tableItems = action.getTableItems();
		boolean comma = false;
		for (TableItem tableItem : tableItems) {
			if (comma) {
				tablesBuilder.append(", ");
			}
			Table table = tableItem.getTable();
			if (table != null) {
				// join 和 limit 同时存在时, 并且存在一对多或多对多的情况下, 分页会出问题.
				// 这里将主表作为基础表(子查询), 特殊处理.
				if (!action.getJoinItems().isEmpty() || action.getLimitItem() != null) {
					List<FilterItem> filterItems = action.getFilterItems();
					// 主表的条件
					List<FilterItem> mainTableFilterItems = filterItems.stream()
							.filter(e -> e.getColumn().getTable().getName().equals(table.getName()))
							.collect(Collectors.toList());
					String mainTableWhereString = toWhere(mainTableFilterItems);
					tablesBuilder.append("(").append(PREFIX_SELECT).append("*").append(PREFIX_FROM)
							.append(table.getName()).append(mainTableWhereString).append(toLimit())
							.append(")");
					// 删除已经在主表中用到的元素
					action.getFilterItems().removeAll(mainTableFilterItems);
					action.setLimitItem(null);
				} else {
					Dialect dialect = dataContext.getDialect();
					if (dialect.assignDatabase()) {
						Schema schema = table.getSchema();
						tablesBuilder.append(schema.getName()).append(".");
					}
					tablesBuilder.append(quote(table.getName()));
				}
			} else {
				tablesBuilder.append(tableItem.getExpression());
			}
			String alias = tableItem.getAlias();
			if (!Strings.isNullOrEmpty(alias)) {
				tablesBuilder.append(ALIAS_KEY).append(alias);
			}
			comma = true;
		}
		return tablesBuilder.toString();
	}

	public String toJoins() {
		List<JoinItem> joinItems = action.getJoinItems();
		if (joinItems.isEmpty()) {
			return "";
		}
		StringBuilder joinsBuilder = new StringBuilder();
		for (JoinItem joinItem : joinItems) {
			switch (joinItem.getJoinType()) {
			case LEFT:
				joinsBuilder.append(LEFT_OUTER_JOIN);
				break;
			case RIGHT:
				joinsBuilder.append(RIGHT_OUTER_JOIN);
				break;
			case INNER:
				joinsBuilder.append(INNER_JOIN);
				break;
			case FULL:
				joinsBuilder.append(FULL_OUTER_JOIN);
				break;

			default:
				throw new DataException(String.format("wrong join type [%s]", joinItem.getJoinType()));
			}
			// join left table
			ColumnItem leftColumnItem = joinItem.getLeftColumn();
			TableItem leftTableItem = leftColumnItem.getTableItem();
			Table leftTable = leftTableItem.getTable();
			String leftTableAlias = leftTableItem.getAlias();
			// join right table
			ColumnItem rightColumnItem = joinItem.getRightColumn();
			TableItem rightTableItem = rightColumnItem.getTableItem();
			Table rightTable = rightTableItem.getTable();
			String rightTableAlias = rightTableItem.getAlias();

			joinsBuilder.append(quote(rightTable.getName()));
			if (!Strings.isNullOrEmpty(rightTableAlias)) {
				joinsBuilder.append(ALIAS_KEY).append(rightTableAlias);
			}
			joinsBuilder.append(JOIN_ON);

			if (Strings.isNullOrEmpty(leftTableAlias)) {
				joinsBuilder.append(quote(leftTable.getName()));
			} else {
				joinsBuilder.append(leftTableAlias);
			}
			joinsBuilder.append(".").append(quote(leftColumnItem.getColumn().getName()));
			joinsBuilder.append(" = ");
			if (Strings.isNullOrEmpty(rightTableAlias)) {
				joinsBuilder.append(quote(rightTable.getName()));
			} else {
				joinsBuilder.append(rightTableAlias);
			}
			joinsBuilder.append(".").append(quote(rightColumnItem.getColumn().getName()));
		}
		return joinsBuilder.toString();
	}

	public String toWhere() {
		return toWhere(action.getFilterItems());
	}

	public String toWhere(List<FilterItem> filterItems) {
		if (filterItems.isEmpty()) {
			return "";
		}
		StringBuilder whereBuilder = new StringBuilder(PREFIX_WHERE);
		boolean first = true;
		for (FilterItem filterItem : filterItems) {
			String filterSql = toFilter(filterItem);
			if (first) {
				// 去掉开头的连接符
				filterSql = deleteFirstConnector(filterSql, filterItem.getConnector());
			}
			whereBuilder.append(filterSql);
			first = false;
		}
		return whereBuilder.toString();
	}

	public String toFilter(FilterItem filterItem) {
		StringBuilder filterBuilder = new StringBuilder();
		filterBuilder.append(" ").append(filterItem.getConnector()).append(" ");
		// 嵌套子条件
		List<FilterItem> subFilterItems = filterItem.getSubFilterItems();
		if (!subFilterItems.isEmpty()) {
			StringBuilder subFilterBuilder = new StringBuilder();
			subFilterItems.forEach(f -> subFilterBuilder.append(toFilter(f)));
			// 去掉开头的连接符
			String subFilterSql = deleteFirstConnector(subFilterBuilder.toString(),
					subFilterItems.get(0).getConnector());
			filterBuilder.append("(").append(subFilterSql).append(")");
			return filterBuilder.toString();
		}
		Column column = filterItem.getColumn();
		if (column == null) {
			filterBuilder.append(filterItem.getExpression());
		} else {
			String tableName = column.getTable().getName();
			String columnName = column.getName();
			filterBuilder.append(quote(tableName)).append(".").append(quote(columnName));
		}
		filterBuilder.append(" ");
		Object value = filterItem.getValue();
		Operator operator = filterItem.getOperator();
		switch (operator) {
		case EQUAL:
		case NOT_EQUAL:
		case LT:
		case LTE:
		case GT:
		case GTE:
		case LIKE:
		case NOT_LIKE:
			filterBuilder.append(operator.sql()).append(" ?");
			params.add(value);
			break;
		case IN:
		case NOT_IN:
			Object[] inValue = (Object[]) value;
			filterBuilder.append(operator.sql()).append(" (?").append(Strings.repeat(",?", inValue.length - 1))
					.append(")");
			params.addAll(Arrays.asList(inValue));
			break;
		case BETWEEN:
			filterBuilder.append("between ? and ?");
			params.addAll(Arrays.asList((Object[]) value));
			break;

		default:
			break;
		}

		return filterBuilder.toString();
	}

	public String toGroups() {
		List<GroupItem> groupItems = action.getGroupItems();
		if (groupItems.isEmpty()) {
			return "";
		}
		StringBuilder groupsBuilder = new StringBuilder(PREFIX_GROUP_BY);
		boolean comma = false;
		for (GroupItem groupItem : groupItems) {
			if (comma) {
				groupsBuilder.append(", ");
			}
			Column column = groupItem.getColumn();
			if (column != null) {
				Table table = column.getTable();
				groupsBuilder.append(quote(table.getName())).append(".").append(quote(column.getName()));
			} else {
				groupsBuilder.append(groupItem.getExpression());
			}
			comma = true;
		}
		return groupsBuilder.toString();
	}

	public String toOrders() {
		List<OrderItem> orderItems = action.getOrderItems();
		if (orderItems.isEmpty()) {
			return "";
		}
		StringBuilder ordersBuilder = new StringBuilder(PREFIX_ORDER_BY);
		boolean comma = false;
		for (OrderItem orderItem : orderItems) {
			if (comma) {
				ordersBuilder.append(", ");
			}
			Column column = orderItem.getColumn();
			if (column != null) {
				Table table = column.getTable();
				ordersBuilder.append(quote(table.getName())).append(".").append(quote(column.getName()));
			} else {
				ordersBuilder.append(orderItem.getExpression());
			}
			switch (orderItem.getOrder()) {
			case DESC:
				ordersBuilder.append(ORDER_DESC);
				break;

			default:
				ordersBuilder.append(ORDER_ASC);
				break;
			}
			comma = true;
		}
		return ordersBuilder.toString();
	}

	public String toLimit() {
		LimitItem limitItem = action.getLimitItem();
		if (limitItem == null) {
			return "";
		}
		StringBuilder limitBuilder = new StringBuilder(PREFIX_LIMIT);
		limitBuilder.append(limitItem.getStart()).append(", ").append(limitItem.getLimit());
		return limitBuilder.toString();
	}

	public String toInsertTable() {
		List<TableItem> tableItems = action.getTableItems();
		StringBuilder tableBuilder = new StringBuilder(PREFIX_INSERT_INTO);
		Table table = tableItems.get(0).getTable();
		tableBuilder.append(table.getSchema().getName()).append(".").append(quote(table.getName()));
		return tableBuilder.toString();

	}

	public String toInsertValues() {
		List<ValueItem> valueItems = action.getValueItems();
		StringBuilder valuesBuilder = new StringBuilder();
		StringBuilder tempColumnsBuilder = new StringBuilder("(");
		StringBuilder tempValuesBuilder = new StringBuilder(" VALUES(");
		boolean comma = false;
		for (ValueItem valueItem : valueItems) {
			if (comma) {
				tempColumnsBuilder.append(", ");
				tempValuesBuilder.append(", ");
			}
			Column column = valueItem.getColumn();
			Object value = valueItem.getValue();
			tempColumnsBuilder.append(column.getName());
			tempValuesBuilder.append("?");
			params.add(value);
			comma = true;
		}
		tempColumnsBuilder.append(")");
		tempValuesBuilder.append(")");
		valuesBuilder.append(tempColumnsBuilder).append(tempValuesBuilder);
		return valuesBuilder.toString();
	}

	public String toUpdateTable() {
		List<TableItem> tableItems = action.getTableItems();
		StringBuilder tableBuilder = new StringBuilder(PREFIX_UPDATE);
		Table table = tableItems.get(0).getTable();
		Dialect dialect = dataContext.getDialect();
		if (dialect.assignDatabase()) {
			Schema schema = table.getSchema();
			tableBuilder.append(schema.getName()).append(".");
		}
		tableBuilder.append(quote(table.getName()));
		return tableBuilder.toString();
	}

	public String toUpdateValues() {
		List<ValueItem> valueItems = action.getValueItems();
		StringBuilder valuesBuilder = new StringBuilder(UPDATE_SET);
		boolean comma = false;
		for (ValueItem valueItem : valueItems) {
			if (comma) {
				valuesBuilder.append(", ");
			}
			Column column = valueItem.getColumn();
			Object value = valueItem.getValue();
			valuesBuilder.append(column.getName()).append(" = ?");
			params.add(value);
			comma = true;
		}
		return valuesBuilder.toString();
	}

	public String toDeleteTable() {
		List<TableItem> tableItems = action.getTableItems();
		StringBuilder tableBuilder = new StringBuilder(PREFIX_DELETE_FROM);
		Table table = tableItems.get(0).getTable();
		tableBuilder.append(table.getSchema().getName()).append(".").append(quote(table.getName()));
		return tableBuilder.toString();
	}

	public String deleteFirstConnector(String sql, Connector connector) {
		switch (connector) {
		case AND:
			return sql.replaceFirst(DELIM_AND, "");
		case OR:
			return sql.replaceFirst(DELIM_OR, "");

		default:
			return sql;
		}
	}

	/**
	 * 用引用符号包裹数据元素
	 * 
	 * @param element
	 * @return
	 */
	public String quote(String element) {
		if (dataContext.getDialect().quoteTable()) {
			return dataContext.getIdentifierQuoteString() + element + dataContext.getIdentifierQuoteString();
		}
		return element;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<Object> getParams() {
		return params;
	}

	public void setParams(List<Object> params) {
		this.params = params;
	}

	public List<Object> getWhereParams() {
		return whereParams;
	}

	public void setWhereParams(List<Object> whereParams) {
		this.whereParams = whereParams;
	}

	public String getCountSql() {
		return countSql;
	}


}
