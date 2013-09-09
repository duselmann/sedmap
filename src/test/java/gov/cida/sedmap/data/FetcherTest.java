package gov.cida.sedmap.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.cida.sedmap.mock.MockContext;
import gov.cida.sedmap.mock.MockDataSource;
import gov.cida.sedmap.mock.MockRequest;
import gov.cida.sedmap.mock.MockResultSet;
import gov.cida.sedmap.mock.MockRowMetaData;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
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
		ctx = new MockContext(ctxenv);

		// populate result set place holders
		ds.put("select * from sedmap.SM_INST_STATIONS", rs);
		ds.put("select * from sedmap.SM_INST_STATIONS", md);
		ds.put("select * from sedmap.SM_DAILY_STATIONS", rs);
		ds.put("select * from sedmap.SM_DAILY_STATIONS", md);
		ds.put("select * from sedmap.SM_INST_SAMPLE", rs);
		ds.put("select * from sedmap.SM_INST_SAMPLE", md);

		// link ctx to data service for testing
		dss = new JdbcFetcher();

		Fetcher.conf = new FetcherConfig() {
			@Override
			protected Context getContext() throws NamingException {
				return ctx;
			}
		}.init();
	}

	@Test
	public void reconditionLine() throws Exception {
		String line = "agency_cd	site_no	datetime	06_00060_00003	06_00060_00003_cd	02_80154_00003	02_80154_00003_cd	03_80155_00003	03_80155_00003_cd";
		String expect = "agency_cd	site_no	datetime	DAILY_FLOW	DAILY_FLOW_QUAL	DAILY_SSC	DAILY_SSC_QUAL	DAILY_SSL	DAILY_SSL_QUAL";
		String actual = dss.reconditionLine(line);
		System.out.println(line);
		System.out.println(actual);

		assertEquals("expect line columns headings replaced", expect, actual);
	}


	//	@Test
	//	public void makeSiteIterator_noHeader() throws Exception {
	//		WriterWithFile writer = IoUtils.createTmpZipWriter("foo","bar");
	//		writer.write("11111|foo\n22222|bar");
	//		writer.close();
	//		FileInputStreamWithFile fis = IoUtils.createTmpZipStream(writer.getFile());
	//		CharSepFormatter pipe = new CharSepFormatter("","|","");
	//
	//		// TODO CLEAN UP
	//		Fetcher test = new Fetcher() {
	//			@Override
	//			public Fetcher initJndiJdbcStore(String jndiJdbc) throws IOException {
	//				return null;
	//			}
	//			@Override
	//			protected InputStream handleNwisData(Iterator<String> sites, FilterWithViewParams filter, Formatter formatter)
	//					throws IOException, SQLException, NamingException {
	//				return null;
	//			}
	//			@Override
	//			protected InputStream handleSiteData(String descriptor, FilterWithViewParams filter, Formatter formatter)
	//					throws IOException, SQLException, NamingException {
	//				return null;
	//			}
	//			@Override
	//			protected InputStream handleDiscreteData(Iterator<String> sites, FilterWithViewParams filter, Formatter formatter)
	//					throws IOException, SQLException, NamingException {
	//				return null;
	//			}
	//		};
	//		Iterator<String> sites = test.makeSiteIterator(fis, pipe);
	//
	//		assertTrue("expect two values", sites.hasNext());
	//		assertEquals("11111",sites.next());
	//		assertTrue("expect one more value", sites.hasNext());
	//		assertEquals("22222",sites.next());
	//		assertFalse("expect no more values", sites.hasNext());
	//
	//		writer.deleteFile();
	//	}
	//
	//
	//
	//	@Test
	//	public void makeSiteIterator_withHeader() throws Exception {
	//		WriterWithFile writer = IoUtils.createTmpZipWriter("foo","bar");
	//		writer.write( "site|name\n11111|foo\n22222|bar");
	//		writer.close();
	//		FileInputStreamWithFile fis = IoUtils.createTmpZipStream(writer.getFile());
	//		CharSepFormatter pipe = new CharSepFormatter("","|","");
	//
	//		// TODO CLEAN UP
	//		Fetcher test = new Fetcher() {
	//			@Override
	//			public Fetcher initJndiJdbcStore(String jndiJdbc) throws IOException {
	//				return null;
	//			}
	//			@Override
	//			protected InputStream handleNwisData(Iterator<String> sites, FilterWithViewParams filter, Formatter formatter)
	//					throws IOException, SQLException, NamingException {
	//				return null;
	//			}
	//			@Override
	//			protected InputStream handleSiteData(String descriptor, FilterWithViewParams filter, Formatter formatter)
	//					throws IOException, SQLException, NamingException {
	//				return null;
	//			}
	//			@Override
	//			protected InputStream handleDiscreteData(Iterator<String> sites, FilterWithViewParams filter, Formatter formatter)
	//					throws IOException, SQLException, NamingException {
	//				return null;
	//			}
	//		};
	//		Iterator<String> sites = test.makeSiteIterator(fis, pipe);
	//
	//		assertTrue("expect two values", sites.hasNext());
	//		assertEquals("11111",sites.next());
	//		assertTrue("expect one more value", sites.hasNext());
	//		assertEquals("22222",sites.next());
	//		assertFalse("expect no more values", sites.hasNext());
	//
	//		writer.deleteFile();
	//	}
	//
	//
	//
	//
	//	@Test
	//	public void makeSiteIterator_withComment() throws Exception {
	//		WriterWithFile writer = IoUtils.createTmpZipWriter("foo","bar");
	//		writer.write("# comment\nsite|name\n11111|foo\n22222|bar");
	//		writer.close();
	//		FileInputStreamWithFile fis = IoUtils.createTmpZipStream(writer.getFile());
	//		CharSepFormatter pipe = new CharSepFormatter("","|","");
	//
	//		// TODO CLEAN UP
	//		Fetcher test = new Fetcher() {
	//			@Override
	//			public Fetcher initJndiJdbcStore(String jndiJdbc) throws IOException {
	//				return null;
	//			}
	//			@Override
	//			protected InputStream handleNwisData(Iterator<String> sites, FilterWithViewParams filter, Formatter formatter)
	//					throws IOException, SQLException, NamingException {
	//				return null;
	//			}
	//			@Override
	//			protected InputStream handleSiteData(String descriptor, FilterWithViewParams filter, Formatter formatter)
	//					throws IOException, SQLException, NamingException {
	//				return null;
	//			}
	//			@Override
	//			protected InputStream handleDiscreteData(Iterator<String> sites, FilterWithViewParams filter, Formatter formatter)
	//					throws IOException, SQLException, NamingException {
	//				return null;
	//			}
	//		};
	//		Iterator<String> sites = test.makeSiteIterator(fis, pipe);
	//
	//		assertTrue("expect two values", sites.hasNext());
	//		assertEquals("11111",sites.next());
	//		assertTrue("expect one more value", sites.hasNext());
	//		assertEquals("22222",sites.next());
	//		assertFalse("expect no more values", sites.hasNext());
	//
	//		writer.deleteFile();
	//	}
	//


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
		params.put("aFilter", expect);
		HttpServletRequest req = new MockRequest(params);
		String actual = dss.getFilter(req,"a");
		assertEquals("getFilter should return what is in that param", expect, actual);
	}
	@Test
	public void getFilter_default() throws Exception {
		params.put("aFilter", null);
		HttpServletRequest req = new MockRequest(params);
		String actual = dss.getFilter(req,"a");
		String expect = "";
		assertEquals("getFilter default should be the empty string not null", expect, actual);
	}
}
