package gov.cida.sedmap.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import gov.cida.sedmap.data.CharSepFormatter.Column;
import gov.cida.sedmap.data.CsvFormatter;
import gov.cida.sedmap.data.Formatter;
import gov.cida.sedmap.data.RdbFormatter;
import gov.cida.sedmap.io.FileDownloadHandler;
import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.MultiPartHandler;
import gov.cida.sedmap.io.util.StrUtils;
import gov.cida.sedmap.mock.MockContext;
import gov.cida.sedmap.mock.MockDataSource;
import gov.cida.sedmap.mock.MockRequest;
import gov.cida.sedmap.mock.MockResponse;
import gov.cida.sedmap.mock.MockResultSet;
import gov.cida.sedmap.mock.MockRowMetaData;
import gov.cida.sedmap.web.DataService.Results;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class DataServiceTest {

	String ogc_v1_0 = "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\"> "
			+ "        <ogc:PropertyIsGreaterThanOrEqualTo>"
			+ "        <ogc:PropertyName>attName</ogc:PropertyName>"
			+ "        <ogc:Literal>5</ogc:Literal>"
			+ "        </ogc:PropertyIsGreaterThanOrEqualTo>"
			+ "        </ogc:Filter>";

	String ogc_v1_1 = "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"><ogc:And><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>REFERENCE_SITE</ogc:PropertyName><ogc:Literal>1</ogc:Literal></ogc:PropertyIsEqualTo><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>SAMPLE_YEARS</ogc:PropertyName><ogc:Literal>19</ogc:Literal></ogc:PropertyIsEqualTo><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>GAGE_BASIN_ID</ogc:PropertyName><ogc:Literal>23534234</ogc:Literal></ogc:PropertyIsEqualTo><ogc:And><ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyName>DA</ogc:PropertyName><ogc:Literal>40</ogc:Literal></ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyIsLessThanOrEqualTo><ogc:PropertyName>DA</ogc:PropertyName><ogc:Literal>400</ogc:Literal></ogc:PropertyIsLessThanOrEqualTo></ogc:And><ogc:And><ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyName>SOIL_K</ogc:PropertyName><ogc:Literal>.5</ogc:Literal></ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyIsLessThanOrEqualTo><ogc:PropertyName>SOIL_K</ogc:PropertyName><ogc:Literal>.7</ogc:Literal></ogc:PropertyIsLessThanOrEqualTo></ogc:And><ogc:Or><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>STATE</ogc:PropertyName><ogc:Literal>WI</ogc:Literal></ogc:PropertyIsEqualTo><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>STATE</ogc:PropertyName><ogc:Literal>Wisconsin</ogc:Literal></ogc:PropertyIsEqualTo></ogc:Or></ogc:And></ogc:Filter>";

	Map<String,Object> ctxenv;
	Map<String,String> params;
	MockContext        ctx;
	MockDataSource     ds;
	MockResultSet      rs;
	MockRowMetaData    md;
	DataService        dss; // instance under test

	boolean nwisHandlerCalled;
	boolean localHandlerCalled;
	int     handleCount;

	@Before
	public void setup() throws Exception {
		// init values
		nwisHandlerCalled  = false;
		localHandlerCalled = false;
		handleCount        = 0;
		ds  = new MockDataSource();
		rs  = new MockResultSet();
		md  = new MockRowMetaData();

		// populate result sets
		ds.put(DataService.SQL_QUERIES.get("discrete_sites"), rs);
		ds.put(DataService.SQL_QUERIES.get("discrete_sites"), md);

		// populate env and params
		params = new HashMap<String, String>();
		ctxenv = new HashMap<String, Object>();
		ctxenv.put(DataService.SEDMAP_DS, ds);
		ctx    = new MockContext(ctxenv);

		// link ctx to data service for testing
		dss = new DataService() {
			private static final long serialVersionUID = 2L;

			@Override
			protected Context getContext() throws javax.naming.NamingException {
				return ctx;
			}
		};
	}


	// override specific behaviors for testing others
	protected void initDataServiceForDoFetchTesting() {
		dss = new DataService() {
			private static final long serialVersionUID = 3L;

			@Override
			protected Context getContext() throws javax.naming.NamingException {
				return ctx;
			}
			@Override
			protected InputStream handleNwisData(String descriptor, String where, Formatter formatter)
					throws IOException, SQLException, NamingException {
				nwisHandlerCalled = true;
				return handleData(descriptor, where, formatter);
			}
			@Override
			protected InputStream handleLocalData(String descriptor, String where, Formatter formatter)
					throws IOException, SQLException, NamingException {
				localHandlerCalled = true;
				return handleData(descriptor, where, formatter);
			}

			protected InputStream handleData(String descriptor, String where, Formatter formatter)
					throws IOException, SQLException, NamingException {
				handleCount++;
				return new ByteArrayInputStream(("Test data for descriptor:'" +descriptor
						+"' and where clause:'" +where +"' file format:'" +formatter.getContentType() +"'").getBytes());
			}
		};
	}


	@Test
	@SuppressWarnings("deprecation")
	public void handleLocalData_csv() throws Exception {
		md.addMetadata( new Column("Site_Id",     Types.VARCHAR, 10) );
		md.addMetadata( new Column("Latitude",    Types.NUMERIC,  3) );
		md.addMetadata( new Column("Longitude",   Types.NUMERIC,  3) );
		md.addMetadata( new Column("create_date", Types.DATE,     8) ); // TODO 8 is a place-holder
		rs.addMockRow("1234567891",40.1,-90.1,new Date(01,1-1,1));
		rs.addMockRow("1234567892",40.2,-90.2,new Date(02,2-1,2));
		rs.addMockRow("1234567893",40.3,-90.3,new Date(03,3-1,3));

		InputStream in = dss.handleLocalData("discrete_sites", "does not matter in this test", new CsvFormatter());
		String actual  = IoUtils.readStream(in);
		//		assertTrue("", new File("discrete_sites.csv"));
		//		System.out.println(actual);

		assertNotNull("data should not be null", actual);
		assertTrue("data should not be empty", actual.trim().length()>0);

		String expect = "Site_Id,Latitude,Longitude,create_date";
		assertTrue("file should contain header row", actual.startsWith(expect));

		assertEquals("expect three rows of data", 3, StrUtils.occurrences("123456789", actual));
		assertEquals("expect each row once", 1, StrUtils.occurrences("1234567891", actual));
		assertEquals("expect each row once", 1, StrUtils.occurrences("1234567892", actual));
		assertEquals("expect each row once", 1, StrUtils.occurrences("1234567893", actual));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void handleLocalData_rdb() throws Exception {
		md.addMetadata( new Column("Site_Id",     Types.VARCHAR, 10) );
		md.addMetadata( new Column("Latitude",    Types.NUMERIC,  3) );
		md.addMetadata( new Column("Longitude",   Types.NUMERIC,  3) );
		md.addMetadata( new Column("create_date", Types.DATE,     8) ); // TODO 8 is a place-holder
		rs.addMockRow("1234567891",40.1,-90.1,new Date(2001,1,1));
		rs.addMockRow("1234567892",40.2,-90.2,new Date(2002,2,2));
		rs.addMockRow("1234567893",40.3,-90.3,new Date(2003,3,3));

		InputStream in = dss.handleLocalData("discrete_sites", "does not matter in this test", new RdbFormatter());
		String actual  = IoUtils.readStream(in);
		//		assertTrue("", new File("discrete_sites.csv"));
		//		System.out.println(actual);

		assertNotNull("data should not be null", actual);
		assertTrue("data should not be empty", actual.trim().length()>0);

		String expect = "Site_Id	Latitude	Longitude	create_date";
		assertEquals("file should contain header row", 1, StrUtils.occurrences(expect, actual));

		assertEquals("expect three rows of data", 3, StrUtils.occurrences("123456789", actual));
		assertEquals("expect each row once", 1, StrUtils.occurrences("1234567891", actual));
		assertEquals("expect each row once", 1, StrUtils.occurrences("1234567892", actual));
		assertEquals("expect each row once", 1, StrUtils.occurrences("1234567893", actual));
	}

	@Test
	public void doFetch_2Files_noNWIS() throws Exception {

		initDataServiceForDoFetchTesting();

		params.put("format", "csv");
		params.put("filter", ogc_v1_0);
		params.put("dataTypes", "daily_discrete_sites"); // requests site info for daily and discrete
		HttpServletRequest    req = new MockRequest(params);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpServletResponse   res = new MockResponse(out);

		FileDownloadHandler handler = new MultiPartHandler(res, out);

		dss.doFetch(req, handler);
		IoUtils.quiteClose(out);

		String actual = out.toString();
		System.out.println(actual);
		assertTrue  ("Daily sites file was expected", actual.contains("filename=daily_sites.csv") );
		assertFalse ("No data file was requested",actual.contains("filename=daily_data") );
		assertTrue  ("Discrete sites file was expected", actual.contains("filename=discrete_sites.csv") );
		assertFalse ("No data file was requested", actual.contains("filename=discrete_data") );
		assertEquals("Expecting the local handler to be called once for each file", 2, handleCount);
		assertFalse ("NWIS handler should not be called", nwisHandlerCalled);
		assertTrue  ("Local handler should have been called", localHandlerCalled);
		assertEquals("Expect one boundary to start, one for each file, and one to end the stream.",
				1+2+1, StrUtils.occurrences(MultiPartHandler.BOUNDARY_TAG, actual));
		assertEquals("The last boundary should be a terminator.",
				actual.length()-MultiPartHandler.BOUNDARY_TAG.length()-2, actual.lastIndexOf(MultiPartHandler.BOUNDARY_TAG+"--"));
	}


	@Test
	public void doFetch_2Files_withNWIS() throws Exception {

		initDataServiceForDoFetchTesting();

		params.put("format", "rdb");
		params.put("filter", ogc_v1_0);
		params.put("dataTypes", "daily_sites_data"); // this requests both site and data for daily
		HttpServletRequest req = new MockRequest(params);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpServletResponse   res = new MockResponse(out);

		FileDownloadHandler handler = new MultiPartHandler(res, out);

		dss.doFetch(req, handler);
		IoUtils.quiteClose(out);

		String actual = out.toString();
		System.out.println(actual);
		assertTrue  ("Daily sites file was expected", actual.contains("filename=daily_sites.rdb") );
		assertTrue  ("NWIS Data file was requested",actual.contains("filename=daily_data.rdb") );
		assertFalse ("Discrete sites file was expected", actual.contains("filename=discrete_sites") );
		assertFalse ("No discrete data file was requested", actual.contains("filename=discrete_data") );
		assertEquals("Expecting the local handler to be called once for each file", 2, handleCount);
		assertTrue  ("NWIS handler should have been called", nwisHandlerCalled);
		assertTrue  ("Local handler should have been called", localHandlerCalled);
		assertEquals("Expect one boundary to start, one for each file, and one to end the stream.",
				1+2+1, StrUtils.occurrences(MultiPartHandler.BOUNDARY_TAG, actual));
		assertEquals("The last boundary should be a terminator.",
				actual.length()-MultiPartHandler.BOUNDARY_TAG.length()-2, actual.lastIndexOf(MultiPartHandler.BOUNDARY_TAG+"--"));
	}


	@Test
	public void getData_ensureAllMocksAreInLine() throws Exception {
		Results r = dss.getData( DataService.SQL_QUERIES.get("discrete_sites") );
		ResultSet actual = r.rs;
		ResultSet expect = rs;
		assertEquals("getData should return the resultset we want", expect, actual);
	}


	@Test
	public void getFormatter_csv() throws Exception {
		params.put("format", "csv");
		HttpServletRequest req = new MockRequest(params);
		String actual = dss.getFormatter(req).getClass().getName();
		String expect = "gov.cida.sedmap.data.CsvFormatter";
		assertEquals("getFormatter should return a CSV formatter", expect, actual);
	}
	@Test
	public void getFormatter_rdb() throws Exception {
		params.put("format", "rdb");
		HttpServletRequest req = new MockRequest(params);
		String actual = dss.getFormatter(req).getClass().getName();
		String expect = "gov.cida.sedmap.data.RdbFormatter";
		assertEquals("getFormatter should return a RDB formatter", expect, actual);
	}
	@Test
	public void getFormatter_default() throws Exception {
		params.put("format", "");
		HttpServletRequest req = new MockRequest(params);
		String actual = dss.getFormatter(req).getClass().getName();
		String expect = "gov.cida.sedmap.data.RdbFormatter";
		assertEquals("getFormatter default should return a RDB formatter", expect, actual);
	}


	@Test
	public void getDataTypes_success() throws Exception {
		String expect = "foo";
		params.put("dataTypes", expect);
		HttpServletRequest req = new MockRequest(params);
		String actual = dss.getDataTypes(req);
		assertEquals("getDataTypes should return what is in that param", expect, actual);
	}
	@Test
	public void getDataTypes_default() throws Exception {
		params.put("dataTypes", null);
		HttpServletRequest req = new MockRequest(params);
		String actual = dss.getDataTypes(req);
		String expect = "daily_discrete_sites";
		assertEquals("getDataTypes default expected", expect, actual);
	}


	@Test
	public void getFilter_success() throws Exception {
		String expect = ogc_v1_0;
		params.put("filter", expect);
		HttpServletRequest req = new MockRequest(params);
		String actual = dss.getFilter(req);
		assertEquals("getFilter should return what is in that param", expect, actual);
	}
	@Test
	public void getFilter_default() throws Exception {
		params.put("filter", null);
		HttpServletRequest req = new MockRequest(params);
		String actual = dss.getFilter(req);
		String expect = "";
		assertEquals("getFilter default should be the empty string not null", expect, actual);
	}


	@Test
	public void ogc2sql_success() throws Exception {
		String actual = dss.ogc2sql(ogc_v1_0);
		String expect = " where ( attName >= 5 )";
		assertEquals("ogc to sql should return an sql where clause", expect, actual);
	}
	@Test
	public void ogc2sql_default() throws Exception {
		String actual = dss.ogc2sql("");
		String expect = "";
		assertEquals("no filter should return no where clause", expect, actual);
	}
	@Test(expected=RuntimeException.class)
	public void ogc2sql_bad() throws Exception {
		dss.ogc2sql("bad ogc");
	}
}
