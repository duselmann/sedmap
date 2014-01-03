package gov.cida.sedmap.data;

import gov.cida.sedmap.io.IoUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CharSepFormatter implements Formatter {

	private final String CONTENT_TYPE;
	private final String FILE_TYPE;
	private final String TYPE;
	private final String SEPARATOR;
        

	public CharSepFormatter(String contentType, String separator, String type) {
		CONTENT_TYPE = contentType;
		SEPARATOR    = separator;
		FILE_TYPE    = "."+type;
		TYPE         = type;
	}



	@Override
	public final String getContentType() {
		return CONTENT_TYPE;
	}
	@Override
	public final String getSeparator() {
		return SEPARATOR;
	}
	@Override
	public String getFileType() {
		return FILE_TYPE;
	}
	@Override
	public String getType() {
		return TYPE;
	}


	@Override
	public String transform(String line, Formatter from) {

		if ( ! SEPARATOR.equals( from.getSeparator() ) ) {
			// String.replaceAll uses regex string and comma is a special char
			while ( line.contains(from.getSeparator()) ) {
				line = line.replace(from.getSeparator(), SEPARATOR);
			}
		}
		return line;
	}

	@Override
	public String fileHeader(List<Column> columns) throws SQLException {
            List<String> columnNames = Arrays.asList(Column.getColumnNames(columns.iterator()));
            return columnHeaders(columnNames.iterator());
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


    public String columnHeaders(Iterator<String> columns) {
    		StringBuilder columnHeaders = new StringBuilder();

		String sep = "";
		while(columns.hasNext()){
                        String column = columns.next();
			columnHeaders.append(sep).append(column);
			sep = SEPARATOR;
		}
		columnHeaders.append( IoUtils.LINE_SEPARATOR );

		return columnHeaders.toString();
    }

    @Override
    public String fileHeader(Iterator<String> columns, HeaderType headerType) throws SQLException {
        StringBuilder header = new StringBuilder();
        header.append(fileHeader(headerType));
        header.append(columnHeaders(columns));
        return header.toString();
    }

    @Override
    public String fileHeader(HeaderType headerType) throws SQLException {
        StringBuilder header = new StringBuilder();
        
        if(HeaderType.GENERAL != headerType){
            //prepend header iff requested header type is not General
            header.append(HeaderType.GENERAL.header);
        }
        header.append(headerType.header);

        return header.toString();
    }
}
