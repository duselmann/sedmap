package gov.cida.sedmap.data;

import java.util.HashMap;
import java.util.Map;

import gov.cida.sedmap.mock.MockContext;
import gov.cida.sedmap.mock.MockDataSource;
import gov.cida.sedmap.mock.MockRequest;
import gov.cida.sedmap.mock.MockResultSet;
import gov.cida.sedmap.mock.MockRowMetaData;
import gov.cida.sedmap.web.DataService;

import javax.servlet.http.HttpServletRequest;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FetcherTest {

	String ogc_v1_0 = "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\"> "
			+ "        <ogc:PropertyIsGreaterThanOrEqualTo>"
			+ "        <ogc:PropertyName>attName</ogc:PropertyName>"
			+ "        <ogc:Literal>5</ogc:Literal>"
			+ "        </ogc:PropertyIsGreaterThanOrEqualTo>"
			+ "        </ogc:Filter>";


	Map<String,Object> ctxenv;
	Map<String,String> params;
	MockContext        ctx;
	MockDataSource     ds;
	MockResultSet      rs;
	MockRowMetaData    md;
	Fetcher            dss; // instance under test

	boolean nwisHandlerCalled;
	boolean localHandlerCalled;
	int     handleCount;

	@BeforeClass
	public static void init() {
		// TODO refactor the need for this
		DataService.setMode("TEST");
	}


	@Before
	public void setup() throws Exception {
		// init values
		nwisHandlerCalled  = false;
		localHandlerCalled = false;
		handleCount        = 0;

		rs  = new MockResultSet();
		md  = new MockRowMetaData();

		// populate env and params
		ds  = new MockDataSource();
		params = new HashMap<String, String>();

		ctxenv = new HashMap<String, Object>();
		ctxenv.put(Fetcher.SEDMAP_DS, ds);
		// link ctx to data service for testing
		DataService.ctx = new MockContext(ctxenv);

		// populate result set place holders
		ds.put("select * from SM_INST_STATIONS", rs);
		ds.put("select * from SM_INST_STATIONS", md);
		ds.put("select * from SM_DAILY_STATIONS", rs);
		ds.put("select * from SM_DAILY_STATIONS", md);
		ds.put("select * from SM_INST_SAMPLE", rs);
		ds.put("select * from SM_INST_SAMPLE", md);

		// link ctx to data service for testing
		dss = new JdbcFetcher();

	}


	@Test
	public void getRegExExpectations_number() throws Exception {
		String site = "11111";
		boolean actual = site.matches("^\\d+$");
		assertTrue("expect number string to match", actual);
	}
	@Test
	public void getRegExExpectations_alphnumeric() throws Exception {
		String site = "11a111";
		boolean actual = site.matches("^\\d+$");
		assertFalse("expect alph char in string not to match", actual);
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
}
