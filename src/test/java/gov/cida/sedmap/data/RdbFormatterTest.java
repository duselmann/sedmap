package gov.cida.sedmap.data;

import static org.junit.Assert.*;

import java.sql.Date;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.mock.MockDataSource;
import gov.cida.sedmap.mock.MockResultSet;
import gov.cida.sedmap.mock.MockRowMetaData;

public class RdbFormatterTest {

	MockDataSource     ds;
	MockResultSet      rs;
	MockRowMetaData    md;
	String sql;

	List<Column> cols;

	@Before
	@SuppressWarnings("deprecation")
	public void setup() throws Exception {
		// init values
		ds   = new MockDataSource();
		rs   = new MockResultSet();
		md   = new MockRowMetaData();

		cols = new ArrayList<Column>();
		cols.add( new Column("Site_Id",     Types.VARCHAR, 10, false) );
		cols.add( new Column("Latitude",    Types.NUMERIC,  3, false) );
		cols.add( new Column("Longitude",   Types.NUMERIC,  3, false) );
		cols.add( new Column("create_date", Types.DATE,     8, false) ); // TODO 8 is a place-holder
		md.addMetadata(cols);

		rs.addMockRow("1234567891",40.1,-90.1,new Date(01,1-1,1));
		rs.addMockRow("1234567892",40.2,-90.2,new Date(02,2-1,2));
		rs.addMockRow("1234567893",40.3,-90.3,new Date(03,3-1,3));

		sql = "select * from dual";
		// populate result sets
		ds.put(sql, rs);
		ds.put(sql, md);
	}



	@Test
	public void getFileHeader() throws Exception {
		RdbFormatter rdb = new RdbFormatter();

		String actual = rdb.fileHeader(cols);
		System.out.println(actual);

		String expect = rdb.getFileHeader();
		assertTrue(actual.startsWith(expect));

		expect = IoUtils.LINE_SEPARATOR+"Site_Id	Latitude	Longitude	create_date"+IoUtils.LINE_SEPARATOR;
		assertTrue(actual.contains(expect));

		expect = IoUtils.LINE_SEPARATOR+"10s	3n	3n	8d"+IoUtils.LINE_SEPARATOR;
		assertTrue(actual.endsWith(expect));
	}

	@Test
	public void getFileRows() throws Exception {
		rs.open();
		rs.next();
		String actual = new RdbFormatter().fileRow(new ResultSetColumnIterator(rs));
		System.out.println(actual);
		String expect = "1234567891\t40.1\t-90.1\t1901-01-01"+IoUtils.LINE_SEPARATOR;
		assertEquals(expect,actual);
	}


	@Test
	public void constructor() throws Exception {
		String contentType   = "text/tab-separated-values";
		String separator     = "\t";
		String fileType      = ".rdb";
		CharSepFormatter frm = new RdbFormatter();

		assertEquals(contentType, frm.getContentType());
		assertEquals(separator, frm.getSeparator());
		assertEquals(fileType, frm.getFileType());

	}


}
