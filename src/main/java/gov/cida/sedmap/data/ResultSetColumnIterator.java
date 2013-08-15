package gov.cida.sedmap.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class ResultSetColumnIterator implements Iterator<String> {

	protected ResultSet rs;
	protected int column;
	protected int count;



	public static class SqlException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public SqlException(SQLException e) {
			super(e);
		}
	}



	public ResultSetColumnIterator(ResultSet rs) {
		this.rs=rs;
		try {
			count = rs.getMetaData().getColumnCount();
		} catch (SQLException e) {
			throw new ResultSetColumnIterator.SqlException(e);
		}
	}

	@Override
	public boolean hasNext() {
		return column < count;
	}

	@Override
	public String next() {
		try {
			return rs.getString(++column);
		} catch (SQLException e) {
			throw new ResultSetColumnIterator.SqlException(e);
		}
	}

	@Override
	public void remove() {
		try {
			rs.deleteRow();
		} catch (SQLException e) {
			throw new ResultSetColumnIterator.SqlException(e);
		}
	}
}
