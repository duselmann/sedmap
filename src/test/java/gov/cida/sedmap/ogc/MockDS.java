package gov.cida.sedmap.ogc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import gov.cida.sedmap.mock.MockConnection;
import gov.cida.sedmap.mock.MockDataSource;
import gov.cida.sedmap.mock.MockPreparedStatement;

public class MockDS extends MockDataSource {
	MockDataSource ds = this;

	@Override
	public MockConnection createMockConnection() {
		MockConnection cn =  new MockConnection(ds) {
			@Override
			public String getCatalog() throws SQLException {
				return "Catalog";
			}
			@Override
			public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
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
		};
		return cn;
	}
}
