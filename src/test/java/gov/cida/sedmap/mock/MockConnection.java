package gov.cida.sedmap.mock;


import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

// this mock connection returns a mock statement
public class MockConnection implements Connection {

	protected MockDataSource dataSource;
	protected boolean closed = false;

	public MockConnection() {
		// might not need a data source during testing
	}
	public MockConnection(MockDataSource ds) {
		dataSource = ds;
	}

	ResultSet getMockResults(String sql) {
		return dataSource.getMockResults(sql);
	}

	@Override
	public void close() throws SQLException {
		closed = true;
	}
	@Override
	public boolean isClosed() throws SQLException {
		return closed;
	}

	@Override
	public Statement createStatement() throws SQLException {
		if (closed) throw new SQLException("Connection closed");
		MockStatement statement = new MockStatement();
		statement.conn = this;
		return statement;
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return dataSource.getMetadata();
	}

	private boolean autocommit;
	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		autocommit = autoCommit;
	}
	@Override
	public boolean getAutoCommit() throws SQLException {
		return autocommit;
	}



	// unimplemented methods //



	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public PreparedStatement prepareStatement(String sql)
			throws SQLException {

		MockPreparedStatement ps = new MockPreparedStatement(sql) {
			Map<Integer, Object> params = new HashMap<Integer, Object>();
			@Override
			public void setFetchSize(int rows) throws SQLException { }

			@Override
			public void setString(int index, String value) throws SQLException {
				params.put(index,value);
			}
		};
		ps.conn = this;
		return ps;
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}


	@Override
	public void commit() throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void rollback() throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean isReadOnly() throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getCatalog() throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void clearWarnings() throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public Statement createStatement(int resultSetType,
			int resultSetConcurrency) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public PreparedStatement prepareStatement(String sql,
			int resultSetType, int resultSetConcurrency)
					throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public int getHoldability() throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
					throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public PreparedStatement prepareStatement(String sql,
			int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
					throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public PreparedStatement prepareStatement(String sql,
			int autoGeneratedKeys) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public PreparedStatement prepareStatement(String sql,
			int[] columnIndexes) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public PreparedStatement prepareStatement(String sql,
			String[] columnNames) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public Clob createClob() throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public Blob createBlob() throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public NClob createNClob() throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public Properties getClientInfo() throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public void setSchema(String schema) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}
	@Override
	public String getSchema() throws SQLException {
		throw new RuntimeException("Not mocked for testing");
	}
	@Override
	public void abort(Executor executor) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}
	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds)
			throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}
	@Override
	public int getNetworkTimeout() throws SQLException {
		throw new RuntimeException("Not mocked for testing");
	}

}

