package io.yawp.driver.postgresql.datastore;

import io.yawp.driver.postgresql.connection.ConnectionPool;
import io.yawp.driver.postgresql.connection.SqlRunner;
import io.yawp.repository.ObjectModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SchemaSynchronizer {

	private static final String SQL_CATALOG_SELECT = "SELECT c.* FROM pg_catalog.pg_class c JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace";

	private static final String SQL_CATALOG_TABLES = "WHERE c.relkind = 'r' AND n.nspname = ANY (CURRENT_SCHEMAS(false))";

	private static final String SQL_CREATE_TABLE = "create table %s (id bigserial primary key, key jsonb, properties jsonb)";

	public static void sync(Set<Class<?>> endpointClazzes) {
		Connection connection = ConnectionPool.connection();

		try {
			List<String> existingTables = getExistingTables(connection);

			for (Class<?> endpointClazz : endpointClazzes) {
				sync(connection, existingTables, endpointClazz);
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			ConnectionPool.close(connection);
		}
	}

	protected static List<String> getExistingTables(Connection connection) throws SQLException {
		String sql = String.format("%s %s", SQL_CATALOG_SELECT, SQL_CATALOG_TABLES);

		SqlRunner runner = new SqlRunner(sql) {
			@Override
			public List<String> collect(ResultSet rs) throws SQLException {
				List<String> tables = new ArrayList<String>();

				while (rs.next()) {
					tables.add(rs.getString("relname"));
				}

				return tables;
			}
		};

		return runner.executeQuery(connection);
	}

	private static void sync(Connection connection, List<String> existingTables, Class<?> endpointClazz) {
		ObjectModel model = new ObjectModel(endpointClazz);

		if (existingTables.contains(model.getKind())) {
			return;
		}

		createTable(connection, model.getKind());
	}

	private static void createTable(Connection connection, String kind) {
		new SqlRunner(String.format(SQL_CREATE_TABLE, kind)).execute(connection);
	}

}
