package gov.cida.sedmap.data;

import gov.cida.sedmap.io.IoUtils;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class CharSepFormatter implements Formatter {

	private final String CONTENT_TYPE;
	private final String FILE_TYPE;
	private final String SEPARATOR;



	public CharSepFormatter(String contentType, String separator, String fileType) {
		CONTENT_TYPE = contentType;
		SEPARATOR    = separator;
		FILE_TYPE    = fileType;
	}



	@Override
	public final String getContentType() {
		return CONTENT_TYPE;
	}
	public final String getSeparator() {
		return SEPARATOR;
	}
	@Override
	public String getFileType() {
		return FILE_TYPE;
	}



	@Override
	public String fileHeader(List<Column> columns) throws SQLException {
		StringBuilder header = new StringBuilder();

		String sep = "";
		for (Column col : columns) {
			header.append(sep).append(col.name);
			sep = SEPARATOR;
		}
		header.append( IoUtils.LINE_SEPARATOR );

		return header.toString();
	}



	@Override
	public String fileRow(Iterator<String> values) throws SQLException {
		StringBuilder row = new StringBuilder();


		String sep = "";
		while (values.hasNext()) {
			// JDBC is one-based
			String val = values.next();
			val = val==null ?"" :val;
			if (val.contains(SEPARATOR)) {
				val = val.contains("\"") ?val.replaceAll("\"", "'") :val;
				val = "\""+val+"\"";
			}
			row.append(sep).append(val);
			sep = SEPARATOR;
		}
		row.append( IoUtils.LINE_SEPARATOR );

		return row.toString();
	}
}
