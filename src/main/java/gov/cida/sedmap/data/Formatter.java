package gov.cida.sedmap.data;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public interface Formatter {
	String getContentType();
	String fileHeader(List<Column> columns) throws SQLException;
	String fileRow(Iterator<String> values) throws SQLException;
	//	String fileHeader(Iterator<String> columns) throws SQLException;
	//	String fileRow(Iterator<Object> data) throws SQLException;
	String getFileType();
	String getSeparator();
	String getType();
	String transform(String line, Formatter from);
}
