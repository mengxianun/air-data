package com.github.mengxianun.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mengxianun.core.AbstractDataContext;
import com.github.mengxianun.core.Action;
import com.github.mengxianun.core.MetaData;
import com.github.mengxianun.core.ResultStatus;
import com.github.mengxianun.core.SQLBuilder;
import com.github.mengxianun.core.attributes.ResultAttributes;
import com.github.mengxianun.core.json.JsonAttributes;
import com.github.mengxianun.core.schema.DefaultColumn;
import com.github.mengxianun.core.schema.DefaultSchema;
import com.github.mengxianun.core.schema.DefaultTable;
import com.github.mengxianun.core.schema.Schema;
import com.github.mengxianun.jdbc.dbutils.handler.JsonArrayHandler;
import com.github.mengxianun.jdbc.dbutils.handler.JsonObjectHandler;
import com.github.mengxianun.jdbc.dbutils.processor.JsonRowProcessor;
import com.github.mengxianun.jdbc.dialect.JdbcDialectFactory;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JdbcDataContext extends AbstractDataContext {

	private static final Logger logger = LoggerFactory.getLogger(JdbcDataContext.class);

	protected DataSource dataSource;

	protected static ThreadLocal<Connection> threadLocalConnection = new ThreadLocal<>();

	protected static ThreadLocal<Boolean> closeConnection = new ThreadLocal<>();

	protected QueryRunner runner;

	protected JsonRowProcessor convert = new JsonRowProcessor();

	public JdbcDataContext() {
	}

	public JdbcDataContext(DataSource dataSource) {
		if (dataSource == null) {
			throw new IllegalArgumentException("DataSource cannot be null");
		}
		this.dataSource = dataSource;
		this.dialect = JdbcDialectFactory.getDialect(dataSource);
		this.runner = new QueryRunner(dataSource);
		closeConnection.set(true);
		initializeMetaData();
	}


	public void initializeMetaData() {
		List<Schema> schemas = new ArrayList<>();
		metaData.setSchemas(schemas);
		// source.add(MetaData.SCHEMAS, schemas);
		try (final Connection connection = getConnection()) {
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			// String databaseProductName = databaseMetaData.getDatabaseProductName();
			// String databaseProductVersion = databaseMetaData.getDatabaseProductVersion();
			// String url = databaseMetaData.getURL();
			String identifierQuoteString = databaseMetaData.getIdentifierQuoteString();

			String defaultSchemaName = connection.getCatalog();

			metaData.setIdentifierQuoteString(identifierQuoteString);
			metaData.setDefaultSchemaName(defaultSchemaName);

			// schema metadata
			ResultSet catalogsResultSet = databaseMetaData.getCatalogs();
			while (catalogsResultSet.next()) {
				String schemaName = catalogsResultSet.getString(1);
				if ("information_schema".equals(schemaName)) {
					continue;
				}

				schemas.add(new DefaultSchema(schemaName));
			}

			// table metadata
			DefaultSchema defaultSchema = (DefaultSchema) metaData.getSchema(defaultSchemaName);
			ResultSet tablesResultSet = databaseMetaData.getTables(defaultSchemaName, null, "%", null);
			while (tablesResultSet.next()) {
				// String tableCatalog = tablesResultSet.getString(1);
				// String tableSchema = tablesResultSet.getString(2);
				String tableName = tablesResultSet.getString(3);
				// String tableType = tablesResultSet.getString(4);
				String remarks = tablesResultSet.getString(5);

				defaultSchema.addTable(new DefaultTable(tableName, defaultSchema, remarks));

			}

			// column metadata
			ResultSet columnsResultSet = databaseMetaData.getColumns(defaultSchemaName, null, "%", null);
			while (columnsResultSet.next()) {
				// String columnCatalog = columnsResultSet.getString(1);
				// String columnSchema = columnsResultSet.getString(2);
				String columnTable = columnsResultSet.getString(3);
				String columnName = columnsResultSet.getString(4);
				// String columnDataType = columnsResultSet.getString(5);
				// String columnTypeName = columnsResultSet.getString(6);
				Integer columnSize = columnsResultSet.getInt(7);
				Boolean columnNullable = columnsResultSet.getBoolean(11);
				String columnRemarks = columnsResultSet.getString(12);
				// Boolean isAutoincrement = columnsResultSet.getBoolean(23);

				DefaultTable table = (DefaultTable) metaData.getTable(defaultSchemaName, columnTable);
				table.addColumn(new DefaultColumn(columnName, table, columnNullable, columnRemarks, columnSize));

			}

		} catch (SQLException e) {
			throw new JdbcDataException(e);
		}
	}

	@Deprecated
	protected void initializeMetaData_old() {
		JsonObject source = new JsonObject();
		JsonObject schemas = new JsonObject();
		source.add(MetaData.SCHEMAS, schemas);
		try (final Connection connection = getConnection()) {
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			String databaseProductName = databaseMetaData.getDatabaseProductName();
			String databaseProductVersion = databaseMetaData.getDatabaseProductVersion();
			String url = databaseMetaData.getURL();
			String identifierQuoteString = databaseMetaData.getIdentifierQuoteString();

			source.addProperty(MetaData.NAME, databaseProductName);
			source.addProperty(MetaData.VERSION, databaseProductVersion);
			source.addProperty(MetaData.URL, url);
			source.addProperty(MetaData.IDENTIFIER_QUOTE_STRING, identifierQuoteString);

			String defaultSchemaName = connection.getCatalog();
			source.addProperty(MetaData.DEFAULT_SCHEMA, defaultSchemaName);

			// schema metadata
			ResultSet catalogsResultSet = databaseMetaData.getCatalogs();
			while (catalogsResultSet.next()) {
				String schemaName = catalogsResultSet.getString(1);
				if ("information_schema".equals(schemaName)) {
					continue;
				}

				JsonObject schema = new JsonObject();
				schema.addProperty(MetaData.NAME, schemaName);

				schemas.add(schemaName, schema);
			}

			// table metadata
			JsonObject defaultSchema = schemas.getAsJsonObject(defaultSchemaName);
			JsonObject tables = new JsonObject();
			defaultSchema.add(MetaData.TABLES, tables);
			ResultSet tablesResultSet = databaseMetaData.getTables(defaultSchemaName, null, "%", null);
			while (tablesResultSet.next()) {
				String tableSchema = tablesResultSet.getString(1);
				String tableName = tablesResultSet.getString(3);
				String tableType = tablesResultSet.getString(4);
				String remarks = tablesResultSet.getString(5);

				JsonObject table = new JsonObject();
				table.addProperty(MetaData.SCHEMA, tableSchema);
				table.addProperty(MetaData.NAME, tableName);
				table.addProperty(MetaData.TYPE, tableType);
				table.addProperty(MetaData.REMARKS, remarks);
				table.add(MetaData.COLUMNS, new JsonObject());

				tables.add(tableName, table);
			}

			// column metadata
			ResultSet columnsResultSet = databaseMetaData.getColumns(defaultSchemaName, null, "%", null);
			while (columnsResultSet.next()) {
				String columnSchema = columnsResultSet.getString(1);
				String columnTable = columnsResultSet.getString(3);
				String columnName = columnsResultSet.getString(4);
				String columnDataType = columnsResultSet.getString(5);
				String columnTypeName = columnsResultSet.getString(6);
				String columnSize = columnsResultSet.getString(7);
				Boolean columnNullable = columnsResultSet.getBoolean(11);
				String columnRemarks = columnsResultSet.getString(12);
				Boolean isAutoincrement = columnsResultSet.getBoolean(23);

				JsonObject column = new JsonObject();
				column.addProperty(MetaData.SCHEMA, columnSchema);
				column.addProperty(MetaData.TABLE, columnTable);
				column.addProperty(MetaData.NAME, columnName);
				column.addProperty(MetaData.COLUMN_DATA_TYPE, columnDataType);
				column.addProperty(MetaData.COLUMN_TYPE_NAME, columnTypeName);
				column.addProperty(MetaData.COLUMN_SIZE, columnSize);
				column.addProperty(MetaData.COLUMN_NULLABLE, columnNullable);
				column.addProperty(MetaData.REMARKS, columnRemarks);
				column.addProperty(MetaData.COLUMN_IS_AUTOINCREMENT, isAutoincrement);

				JsonObject table = tables.getAsJsonObject(columnTable);
				JsonObject columns = table.getAsJsonObject(MetaData.COLUMNS);
				columns.add(columnName, column);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void startTransaction() {
		Connection conn = threadLocalConnection.get();
		if (conn == null) {
			try {
				conn = dataSource.getConnection();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			threadLocalConnection.set(conn);
		}
		try {
			conn.setAutoCommit(false);
			closeConnection.set(false);
			if (logger.isDebugEnabled()) {
				logger.debug("Start new transaction.");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Connection getConnection() throws SQLException {
		Connection conn = threadLocalConnection.get();
		if (conn == null) {
			try {
				return dataSource.getConnection();
			} catch (SQLException e) {
				throw new JdbcDataException("Could not establish connection", e);
			}
		} else {
			return conn;
		}
	}

	public void commit() throws SQLException {
		Connection conn = threadLocalConnection.get();
		if (conn != null) {
			try {
				conn.commit();
				if (logger.isDebugEnabled()) {
					logger.debug("Transaction commit.");
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void rollback() throws SQLException {
		Connection conn = threadLocalConnection.get();
		if (conn != null) {
			try {
				conn.rollback();
				if (logger.isDebugEnabled()) {
					logger.debug("Transaction rollback.");
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void close() {
		Connection conn = threadLocalConnection.get();
		if (conn != null) {
			try {
				conn.close();
				if (logger.isDebugEnabled()) {
					logger.debug("Transaction close.");
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				threadLocalConnection.remove();
				closeConnection.set(true);
			}
		}
	}

	/**
	 * 指定一组事务操作
	 * 
	 * @param atoms
	 * @throws SQLException
	 */
	public void trans(Atom... atoms) throws SQLException {
		if (null == atoms) {
			return;
		}
		try {
			startTransaction();
			for (Atom atom : atoms) {
				atom.run();
			}
			commit();
		} catch (Exception e) {
			rollback();
			throw e;
		} finally {
			close();
		}
	}

	@Override
	public JsonElement action(Action action) {
		JsonElement result = null;
		SQLBuilder sqlBuilder = new SQLBuilder(action);
		String sql = sqlBuilder.getSql();
		List<Object> params = sqlBuilder.getParams();
		try {
			if (action.isDetail()) {
				result = runner.query(sql, new JsonObjectHandler(convert), params.toArray());
			} else if (action.isSelect()) {
				result = runner.query(sql, new JsonArrayHandler(convert), params.toArray());
			} else if (action.isInsert()) {
				Object primaryKey = runner.insert(sql, new ScalarHandler<>(), params.toArray());
				JsonObject jsonObject = new JsonObject();
				jsonObject.add(ResultAttributes.PRIMARY_KEY.toString().toLowerCase(),
						new Gson().toJsonTree(primaryKey));
				result = jsonObject;
			} else if (action.isUpdate() || action.isDelete()) {
				int count = runner.update(sql, params.toArray());
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty(ResultAttributes.COUNT.toString().toLowerCase(), count);
				result = jsonObject;
			}
		} catch (SQLException e) {
			logger.error(ResultStatus.DATASOURCE_SQL_FAILED.message(), e);
			throw new JdbcDataException(ResultStatus.DATASOURCE_SQL_FAILED.fill(sql));
		}
		if (logger.isDebugEnabled()) {
			logger.debug("SQL: {}", sql);
			logger.debug("Params: {}", params);
		}
		return result;
	}

	@Override
	public JsonElement action(Action... actions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonElement executeNative(String script) {
		JsonElement result = null;
		script = script.trim();
		try {
			if (script.startsWith(JsonAttributes.SELECT)) {
				result = runner.query(script, new JsonArrayHandler());
			} else if (script.startsWith(JsonAttributes.INSERT)) {
				Object primaryKey = runner.insert(script, new ScalarHandler<>());
				JsonObject jsonObject = new JsonObject();
				jsonObject.add(ResultAttributes.PRIMARY_KEY.toString().toLowerCase(),
						new Gson().toJsonTree(primaryKey));
				result = jsonObject;
			} else if (script.startsWith(JsonAttributes.UPDATE) || script.startsWith(JsonAttributes.DELETE)) {
				int count = runner.update(script);
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty(ResultAttributes.COUNT.toString().toLowerCase(), count);
				result = jsonObject;
			}
		} catch (SQLException e) {
			logger.error(ResultStatus.NATIVE_FAILED.message(), e);
			throw new JdbcDataException(ResultStatus.NATIVE_FAILED);
		}
		return result;
	}

}
