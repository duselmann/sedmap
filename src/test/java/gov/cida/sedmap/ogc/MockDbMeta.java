package gov.cida.sedmap.ogc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import gov.cida.sedmap.mock.MockDbMetaData;
import gov.cida.sedmap.mock.MockResultSet;

public class MockDbMeta extends MockDbMetaData {
	@Override
	public ResultSet getTables(String catalog, String schemaPattern,
			String tableNamePattern, String[] types) throws SQLException {
		MockResultSet rs = new MockResultSet() {
			@Override
			public String getString(String columnName) throws SQLException {
				//						int col = getCol(columnLabel);
				if (columnName.equals("TABLE_SCHEM")) return (String) current[0];
				if (columnName.equals("TABLE_NAME")) return (String) current[1];
				return null;
			}
		};
		rs.addMockRow("schema","TABLENAME");
		rs.open();

		return rs;
	}
	@Override
	public ResultSet getTableTypes() throws SQLException {
		MockResultSet rs = new MockResultSet() {
			@Override
			public String getString(String columnName) throws SQLException {
				if (columnName.equals("TABLE_TYPE")) return (String) current[0];
				return null;
			}
		};

		rs.addMockRow("TABLE");
		rs.addMockRow("VIEW");
		rs.addMockRow("SYNONYM");
		rs.open();

		return rs;
	}
	@Override
	public ResultSet getColumns(String catalog,
			String schemaPattern,
			String tableNamePattern,
			String columnNamePattern)
					throws SQLException {
		MockResultSet rs = new MockResultSet() {
			@Override
			public String getString(String columnName) throws SQLException {
				MockColumn column = (MockColumn) current[0];
				if ("TABLE_NAME".equals(columnName))
					return "TABLENAME";
				if ("COLUMN_NAME".equals(columnName))
					return column.name;
				if (  "TYPE_NAME".equals(columnName))
					return column.typeName;
				if ("IS_NULLABLE".equals(columnName))
					return column.nullable?"YES":"NO";
				return null;
			}
			@Override
			public String getString(int index) throws SQLException {
				switch (index) {
				case 3: return getString("TABLE_NAME");
				case 4: return getString("COLUMN_NAME");
				case 6: return getString("TYPE_NAME");
				}
				return null;
			}
			@Override
			public int getInt(String columnName) throws SQLException {
				MockColumn column = (MockColumn) current[0];
				if ("DATA_TYPE".equals(columnName)) return column.type;
				return -1;
			}
		};

		rs.addMockRow( new MockColumn("Site_Id",     Types.VARCHAR, 10, false, "NVARCHAR2") );
		rs.addMockRow( new MockColumn("Latitude",    Types.NUMERIC,  3, false, "NUMBER") );
		rs.addMockRow( new MockColumn("Longitude",   Types.NUMERIC,  3, false, "NUMBER") );
		rs.addMockRow( new MockColumn("create_date", Types.DATE,     8, false, "DATE") ); // TODO 8 is a place-holder
		rs.open();

		return rs;
	}

	@Override
	public ResultSet getPrimaryKeys(String catalog, String schema, String table)
			throws SQLException {
		MockResultSet rs = new MockResultSet() {
			@Override
			public String getString(String columnName) throws SQLException {
				if ("TABLE_NAME".equals(columnName))
					return "TABLENAME";
				if ("COLUMN_NAME".equals(columnName))
					return current[0].toString();
				if (  "TYPE_NAME".equals(columnName))
					return current[0].toString();
				if ("IS_NULLABLE".equals(columnName))
					return "NO";
				return null;
			}
		};

		rs.addMockRow("Site_Id");
		rs.open();

		return rs;
	}

}
