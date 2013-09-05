package gov.cida.sedmap.mock;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class MockDataSource implements DataSource {

	private Map<String, MockResultSet> mockResults = new HashMap<String, MockResultSet>();

	private MockDbMetaData metadata;

	public DatabaseMetaData getMetadata() {
		return metadata;
	}

	public void put(String sql, MockResultSet results) {
		mockResults.put(sql, results);
	}
	MockResultSet getMockResults(String sql) {
		for (String key : mockResults.keySet()) {
			if (sql.startsWith(key) || sql.equals(key)) {
				MockResultSet rs = mockResults.get(key);
				rs.open();
				return rs;
			}
		}
		// this should be hard so if you forget to inject a result set you fail early
		throw new RuntimeException("No matching mock results sets found for - " + sql);
	}


	public void put(String sql, ResultSetMetaData metadata) {
		mockResults.get(sql).metadata = metadata;
	}


	public void setMetaData(MockDbMetaData metaData) {
		metadata = metaData;
	}


	@Override
	public Connection getConnection() throws SQLException {
		MockConnection cn = createMockConnection();
		return cn;
	}
	// Override this in you tests
	public MockConnection createMockConnection() {
		return new MockConnection(this);
	}



	// unimplemented methods //



	@Override
	public PrintWriter getLogWriter() throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public int getLoginTimeout() throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new RuntimeException("Not mocked for testing");
	}

}
