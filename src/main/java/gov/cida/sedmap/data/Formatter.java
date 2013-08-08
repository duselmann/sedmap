package gov.cida.sedmap.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Formatter {
	String getContentType();
	String fileHeader(ResultSet rs) throws SQLException;
	String fileRow(ResultSet rs) throws SQLException;
}
