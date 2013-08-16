package gov.cida.sedmap.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import gov.cida.sedmap.data.Column;
import gov.cida.sedmap.data.CsvFormatter;
import gov.cida.sedmap.data.Formatter;
import gov.cida.sedmap.data.JdbcFetcher;
import gov.cida.sedmap.data.JdbcFetcher.Results;
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
import gov.cida.sedmap.web.DataService;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;

import org.geotools.data.jdbc.FilterToSQL;
import org.geotools.data.jdbc.FilterToSQLException;
import org.geotools.factory.GeoTools;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.filter.Filter;

public class JdbcFetcherTest {

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
	JdbcFetcher        fetcher; // instance under test

	boolean nwisHandlerCalled;
	boolean localHandlerCalled;
	int     handleCount;

	@BeforeClass
	public static void init() {
		// TODO refactor the need for this
		DataService.setMode("TEST");
	}


	@Before
	@SuppressWarnings("deprecation")
	public void setup() throws Exception {
		// init values
		nwisHandlerCalled  = false;
		localHandlerCalled = false;
		handleCount        = 0;

		rs  = new MockResultSet();
		rs.addMockRow("1234567891",40.1,-90.1,new Date(01,1-1,1));
		rs.addMockRow("1234567892",40.2,-90.2,new Date(02,2-1,2));
		rs.addMockRow("1234567893",40.3,-90.3,new Date(03,3-1,3));

		md  = new MockRowMetaData();
		md.addMetadata( new Column("Site_Id",     Types.VARCHAR, 10, false) );
		md.addMetadata( new Column("Latitude",    Types.NUMERIC,  3, false) );
		md.addMetadata( new Column("Longitude",   Types.NUMERIC,  3, false) );
		md.addMetadata( new Column("create_date", Types.DATE,     8, false) ); // TODO 8 is a place-holder

		// populate env and params
		ds  = new MockDataSource();
		params = new HashMap<String, String>();
		ctxenv = new HashMap<String, Object>();
		ctxenv.put(Fetcher.SEDMAP_DS, ds);
		// link ctx to data service for testing
		DataService.ctx = new MockContext(ctxenv);
		Field geoToolsCtx = GeoTools.class.getDeclaredField("context");
		geoToolsCtx.setAccessible(true);
		geoToolsCtx.set(null, DataService.ctx);

		// populate result sets
		ds.put("select * from SM_INST_STATIONS", rs);
		ds.put("select * from SM_INST_STATIONS", md);
		ds.put("select * from SM_DAILY_STATIONS", rs);
		ds.put("select * from SM_DAILY_STATIONS", md);
		// populate result set place holders
		ds.put("select * from SM_INST_SAMPLE", new MockResultSet());
		ds.put("select * from SM_INST_SAMPLE", new MockRowMetaData());

		// link ctx to data service for testing
		fetcher = new JdbcFetcher();

	}


	// override specific behaviors for testing others
	protected void initJdbcFetcherForDoFetchTesting() {
		fetcher = new JdbcFetcher() {
			@Override
			protected InputStream handleNwisData(Iterator<String> sites, Filter filter, Formatter formatter)
					throws IOException, SQLException, NamingException {
				nwisHandlerCalled = true;
				return handleData("NWIS", filter, formatter);
			}
			@Override
			protected InputStream handleLocalData(String descriptor, Filter filter, Formatter formatter)
					throws IOException, SQLException, NamingException {
				localHandlerCalled = true;
				return handleData(descriptor, filter, formatter);
			}

			protected InputStream handleData(String descriptor, Filter filter, Formatter formatter)
					throws IOException, SQLException, NamingException {
				handleCount++;

				String where = "";

				if (filter != null) {
					try {
						where = new FilterToSQL().encodeToString(filter);
					} catch (FilterToSQLException e) {
						throw new SQLException("Failed to convert filter to sql where clause.",e);
					}
				}
				return new ByteArrayInputStream(("Test data for descriptor:'" +descriptor
						+"' and where clause:'" +where +"' file format:'" +formatter.getContentType() +"'").getBytes());
			}
		};
	}


	@Test
	public void handleLocalData_csv() throws Exception {
		InputStream in = fetcher.handleLocalData("discrete_sites", Filter.INCLUDE, new CsvFormatter());
		String actual  = IoUtils.readStream(in);
		//		assertTrue("", new File("discrete_sites.csv"));
		//		System.out.println(actual);

		assertNotNull("data should not be null", actual);
		assertTrue("data should not be empty", actual.trim().length()>0);

		String expect = "Site_Id,Latitude,Longitude,create_date";
		System.out.println(actual);
		assertTrue("file should contain header row", actual.startsWith(expect));

		assertEquals("expect three rows of data", 3, StrUtils.occurrences("123456789", actual));
		assertEquals("expect each row once", 1, StrUtils.occurrences("1234567891", actual));
		assertEquals("expect each row once", 1, StrUtils.occurrences("1234567892", actual));
		assertEquals("expect each row once", 1, StrUtils.occurrences("1234567893", actual));
	}

	@Test
	public void handleLocalData_rdb() throws Exception {
		InputStream in = fetcher.handleLocalData("discrete_sites", Filter.INCLUDE, new RdbFormatter());
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

		initJdbcFetcherForDoFetchTesting();

		params.put("format", "csv");
		params.put("filter", ogc_v1_0);
		params.put("dataTypes", "daily_discrete_sites"); // requests site info for daily and discrete
		HttpServletRequest    req = new MockRequest(params);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpServletResponse   res = new MockResponse(out);

		FileDownloadHandler handler = new MultiPartHandler(res, out);

		fetcher.doFetch(req, handler);
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

		initJdbcFetcherForDoFetchTesting();

		params.put("format", "rdb");
		params.put("filter", ogc_v1_0);
		params.put("dataTypes", "daily_sites_data"); // this requests both site and data for daily
		HttpServletRequest req = new MockRequest(params);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpServletResponse   res = new MockResponse(out);

		FileDownloadHandler handler = new MultiPartHandler(res, out);

		fetcher.doFetch(req, handler);
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
	public void getData_ensureMocksAreInLine() throws Exception {
		Results r = fetcher.getData( "select * from " +Fetcher.DATA_TABLES.get("discrete_sites") );
		ResultSet actual = r.rs;
		ResultSet expect = rs;
		assertEquals("getData should return the resultset we want", expect, actual);
	}

}