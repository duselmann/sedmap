package gov.cida.sedmap.data;

import gov.cida.sedmap.util.IoUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CharSepFormatter implements Formatter {
	private final String CONTENT_TYPE;
	private final String SEPARATOR;

	private List<Column> columnData;

	public final static class Column {
		public final String name;
		public final int    type;
		public final int    size;
		public Column(String name, int type, int size) {
			this.name = name;
			this.type = type;
			this.size = size;
		}
	}


	public CharSepFormatter(String contentType, String separator) {
		CONTENT_TYPE = contentType;
		SEPARATOR    = separator;
	}


	@Override
	public final String getContentType() {
		return CONTENT_TYPE;
	}
	public final String getSeparator() {
		return SEPARATOR;
	}


	public final List<Column> getTableColumns(ResultSet rs) throws SQLException {
		if (columnData != null) return columnData;

		columnData = new ArrayList<Column>();

		ResultSetMetaData md = rs.getMetaData();

		int columnCount = md.getColumnCount();
		for (int c = 1; c<= columnCount; c++) {
			String name = md.getColumnName(c);
			int    type = md.getColumnType(c);
			int    size = md.getScale(c);

			columnData.add( new Column(name, type, size) );
		}

		return columnData;
	}


	public String fileHeader(ResultSet rs) throws SQLException {
		StringBuilder header = new StringBuilder();

		List<Column> cols = getTableColumns(rs);

		String sep = "";
		for (Column col : cols) {
			header.append(sep).append(col.name);
			sep = SEPARATOR;
		}
		header.append( IoUtils.LINE_SEPARATOR );

		return header.toString();
	}


	public String fileRow(ResultSet rs) throws SQLException {
		StringBuilder row = new StringBuilder();

		List<Column> cols = getTableColumns(rs);

		String sep = "";
		for (int c=1; c<=cols.size(); c++) {
			// JDBC is one-based
			row.append(sep).append( rs.getString(c) );
			sep = SEPARATOR;
		}
		row.append( IoUtils.LINE_SEPARATOR );

		return row.toString();
	}

}
