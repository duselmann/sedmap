package gov.cida.sedmap.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Column {
	public final String name;
	public final int    type;
	public final int    size;
	public final boolean nullable;

	public Column(String name, int type, int size, boolean nullable) {
		this.name = name;
		this.type = type;
		this.size = size;

		this.nullable = nullable;
	}
        public static String[] getColumnNames(Iterator<Column> columns){
            LinkedList<String> colNames = new LinkedList<>();
            while(columns.hasNext()){
                Column col = columns.next();
                colNames.add(col.name);
            }
            String[] columnNames = colNames.toArray(new String[0]);
            return (columnNames);
        }

}
