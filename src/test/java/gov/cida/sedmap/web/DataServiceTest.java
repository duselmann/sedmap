package gov.cida.sedmap.web;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import gov.cida.sedmap.mock.MockContext;
import gov.cida.sedmap.mock.MockDataSource;
import gov.cida.sedmap.mock.MockRequest;
import gov.cida.sedmap.mock.MockResultSet;
import gov.cida.sedmap.web.DataService.Results;

import javax.naming.Context;
import javax.servlet.http.HttpServletRequest;

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
	DataService        dss; // instance under test

	@Before
	public void setup() throws Exception {
		ds  = new MockDataSource();
		rs  = new MockResultSet();
		ds.put(DataService.SQL_QUERIES.get("descrete_sites"), rs);

		params = new HashMap<String, String>();
		ctxenv = new HashMap<String, Object>();
		ctxenv.put(DataService.SEDMAP_DS, ds);

		ctx = new MockContext(ctxenv);

		dss = new DataService() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Context getContext() throws javax.naming.NamingException {
				return ctx;
			}
		};
	}


	@Test
	public void getData_ensureAllMocksAreInLine() throws Exception {
		Results r = dss.getData( DataService.SQL_QUERIES.get("descrete_sites") );
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
