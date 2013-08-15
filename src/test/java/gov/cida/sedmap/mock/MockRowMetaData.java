package gov.cida.sedmap.mock;

import gov.cida.sedmap.data.Column;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class MockRowMetaData implements ResultSetMetaData {

	protected List<Column> metadata = new LinkedList<Column>();

	public void addMetadata(Column col) {
		metadata.add(col);
	}
	public void addMetadata(List<Column> cols) {
		metadata.addAll(cols);
	}

	@Override
	public int getColumnCount() throws SQLException {
		return metadata.size();
	}

	@Override
	public String getColumnName(int column) throws SQLException {
		return metadata.get(column-1).name;
	}

	@Override
	public int getColumnType(int column) throws SQLException {
		return metadata.get(column-1).type;
	}
	@Override
	public int getScale(int column) throws SQLException {
		return metadata.get(column-1).size;
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
	public boolean isAutoIncrement(int column) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean isCaseSensitive(int column) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean isSearchable(int column) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean isCurrency(int column) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public int isNullable(int column) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean isSigned(int column) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public int getColumnDisplaySize(int column) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getColumnLabel(int column) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getSchemaName(int column) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public int getPrecision(int column) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getTableName(int column) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getCatalogName(int column) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getColumnTypeName(int column) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean isReadOnly(int column) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean isWritable(int column) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public boolean isDefinitelyWritable(int column) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

	@Override
	public String getColumnClassName(int column) throws SQLException {
		throw new RuntimeException("Not mocked for testing");

	}

}
