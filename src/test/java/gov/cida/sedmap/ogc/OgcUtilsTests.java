package gov.cida.sedmap.ogc;

import gov.cida.sedmap.data.Fetcher;
import gov.cida.sedmap.io.IoUtils;
import gov.cida.sedmap.io.util.StrUtils;
import gov.cida.sedmap.mock.MockContext;
import gov.cida.sedmap.mock.MockDataSource;
import gov.cida.sedmap.mock.MockDbMetaData;
import gov.cida.sedmap.mock.MockResultSet;
import gov.cida.sedmap.mock.MockRowMetaData;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.factory.GeoTools;
import org.geotools.filter.AbstractFilter;
import org.geotools.jdbc.JDBCDataStoreFactory;
import org.geotools.jdbc.JDBCFeatureReader;
import org.geotools.jdbc.JDBCJNDIDataStoreFactory;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import static org.junit.Assert.*;

public class OgcUtilsTests {

	String ogc_v1_0 = "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\"> "
			+ "        <ogc:PropertyIsGreaterThanOrEqualTo>"
			+ "        <ogc:PropertyName>Site_Id</ogc:PropertyName>"
			+ "        <ogc:Literal>5</ogc:Literal>"
			+ "        </ogc:PropertyIsGreaterThanOrEqualTo>"
			+ "        </ogc:Filter>";
	String sql_1_0   = "SELECT SITE_ID,LATITUDE,LONGITUDE,CREATE_DATE FROM TABLENAME";
	String where_1_0 = "Site_Id >= ?";

	String ogc_v1_1 = "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"><ogc:And><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>REFERENCE_SITE</ogc:PropertyName><ogc:Literal>1</ogc:Literal></ogc:PropertyIsEqualTo><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>SAMPLE_YEARS</ogc:PropertyName><ogc:Literal>19</ogc:Literal></ogc:PropertyIsEqualTo><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>GAGE_BASIN_ID</ogc:PropertyName><ogc:Literal>23534234</ogc:Literal></ogc:PropertyIsEqualTo><ogc:And><ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyName>DA</ogc:PropertyName><ogc:Literal>40</ogc:Literal></ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyIsLessThanOrEqualTo><ogc:PropertyName>DA</ogc:PropertyName><ogc:Literal>400</ogc:Literal></ogc:PropertyIsLessThanOrEqualTo></ogc:And><ogc:And><ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyName>yr1</ogc:PropertyName><ogc:Literal>1900</ogc:Literal></ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyIsLessThanOrEqualTo><ogc:PropertyName>yr2</ogc:PropertyName><ogc:Literal>1950</ogc:Literal></ogc:PropertyIsLessThanOrEqualTo></ogc:And><ogc:Or><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>STATE</ogc:PropertyName><ogc:Literal>WI</ogc:Literal></ogc:PropertyIsEqualTo><ogc:PropertyIsEqualTo matchCase=\"true\"><ogc:PropertyName>STATE</ogc:PropertyName><ogc:Literal>Wisconsin</ogc:Literal></ogc:PropertyIsEqualTo></ogc:Or></ogc:And></ogc:Filter>";
	//WHERE (REFERENCE_SITE = 1 AND SAMPLE_YEARS = 19 AND GAGE_BASIN_ID = '23534234' AND (DA >= 40 AND DA <= 400) AND (SOIL_K >= 0.5 AND SOIL_K <= 0.7) AND (STATE = 'WI' OR STATE = 'Wisconsin'))

	String sql_pk_meta = "SELECT Site_Id FROM TABLENAME WHERE 0=1";
	String sql_pk_seqU = "SELECT * FROM USER_SEQUENCES WHERE SEQUENCE_NAME = 'TABLENAME_SITE_ID_SEQUENCE'";
	String sql_pk_seqA = "SELECT * FROM ALL_SEQUENCES WHERE SEQUENCE_NAME = 'TABLENAME_SITE_ID_SEQUENCE'";



	Map<String, Object> dataStoreEnv;
	Map<String,Object>  ctxenv;
	Map<String,String>  params;
	MockContext         ctx;
	MockDataSource      ds;
	MockResultSet       rs;
	MockRowMetaData     md;
	MockDbMetaData      dbmd;


	@Before
	@SuppressWarnings("deprecation")
	public void setup() throws Exception {
		dataStoreEnv = new HashMap<String, Object>();
		// dataStoreEnv.put( JDBCDataStoreFactory.SCHEMA.getName(), "sedmap"); // OPTIONAL
		dataStoreEnv.put( JDBCDataStoreFactory.DBTYPE.getName(), "oracle");
		dataStoreEnv.put( JDBCDataStoreFactory.EXPOSE_PK.getName(), true);
		dataStoreEnv.put( JDBCJNDIDataStoreFactory.JNDI_REFNAME.getName(), Fetcher.SEDMAP_DS);

		// init values
		ds  = new MockDS();
		rs  = new MockResultSet() {
			@Override
			public Object getObject(int columnIndex) throws SQLException {
				return current[columnIndex-1];
			}
		};
		md  = new MockRowMetaData() {
			@Override
			public boolean isAutoIncrement(int column) throws SQLException {
				return false;
			}
		};
		dbmd= new MockDbMeta();


		// populate result sets
		ds.put(sql_1_0, rs);
		ds.put(sql_1_0, md);
		ds.put(sql_pk_meta, rs);
		ds.put(sql_pk_meta, md);
		// the pk is a string not a sequence
		ds.put(sql_pk_seqU, new MockResultSet());
		ds.put(sql_pk_seqA, new MockResultSet());

		ds.setMetaData(dbmd);

		rs.addMockRow("1234567891",40.1,-90.1,new Date(01,1-1,1));
		rs.addMockRow("1234567892",40.2,-90.2,new Date(02,2-1,2));
		rs.addMockRow("1234567893",40.3,-90.3,new Date(03,3-1,3));

		// populate env and params
		params = new HashMap<String, String>();
		ctxenv = new HashMap<String, Object>();
		ctxenv.put(Fetcher.SEDMAP_DS, ds);
		ctx    = new MockContext(ctxenv);

		Field geoToolsCtx = GeoTools.class.getDeclaredField("context");
		geoToolsCtx.setAccessible(true);
		geoToolsCtx.set(null, ctx);

	}





	@Test
	public void test_gt_filter_extract_expression() throws Exception {

		Filter filter = OgcUtils.ogcXml2Filter(ogc_v1_1);
		assertEquals("org.geotools.filter.AndImpl", filter.getClass().getName());
		System.out.println(filter.toString());

		FilterWrapper wrapper = new FilterWrapper( (AbstractFilter) filter );
		assertFalse("expected a logical filter", wrapper.isaMathFilter() );
		assertTrue("expected a logical filter",  wrapper.isaLogicFilter() );
	}
	@Test
	public void test_findFilter() throws Exception {
		Filter filter = OgcUtils.ogcXml2Filter(ogc_v1_1);

		Filter year1 = OgcUtils.findFilter(filter, "yr1");
		assertNotNull(year1);
		assertTrue(year1.toString().contains("yr1"));

		Filter year2 = OgcUtils.findFilter(filter, "yr2");
		assertNotNull(year1);
		assertTrue(year2.toString().contains("yr2"));
	}
	@Test
	public void test_findFilterValue() throws Exception {

		Filter filter = OgcUtils.ogcXml2Filter(ogc_v1_1);

		String yr1_actual = OgcUtils.findFilterValue(filter, "yr1");
		assertEquals("1900", yr1_actual);

		String yr2_actual = OgcUtils.findFilterValue(filter, "yr2");
		assertEquals("1950", yr2_actual);
	}




	@Test
	public void test_gt_dataStore_utils() throws Exception {
		DataStore store = OgcUtils.jndiOracleDataStore(Fetcher.SEDMAP_DS);
		Filter filter   = OgcUtils.ogcXml2Filter(ogc_v1_0);
		JDBCFeatureReader reader = OgcUtils.executeQuery(store, "TABLENAME", filter);

		StringBuilder buf = new StringBuilder();
		while (reader.hasNext()) {

			FeatureValueIterator values = new FeatureValueIterator(reader.next());

			for (String value : values) {
				buf.append(value);
				buf.append(", ");
			}
			buf.append(IoUtils.LINE_SEPARATOR);
		}
		System.out.println(buf);

		assertTrue("expect to find site id 1",buf.indexOf("1234567891")>-1);
		assertTrue("expect to find site id 2",buf.indexOf("1234567892")>-1);
		assertTrue("expect to find site id 3",buf.indexOf("1234567893")>-1);
		assertEquals("expect three rows of data", 3, StrUtils.occurrences("123456789", buf.toString()));
	}



	@Test
	public void test_gt_dataStore_create() throws Exception {
		DataStore store =  DataStoreFinder.getDataStore(dataStoreEnv);
		assertNotNull(store);
	}

	@Test
	public void test_gt_dataStore_raw_access() throws Exception {
		dataStoreEnv = new HashMap<String, Object>();
		// dataStoreEnv.put( JDBCDataStoreFactory.SCHEMA.getName(), "sedmap"); // OPTIONAL
		dataStoreEnv.put( JDBCDataStoreFactory.DBTYPE.getName(), "oracle");
		dataStoreEnv.put( JDBCDataStoreFactory.EXPOSE_PK.getName(), true);
		dataStoreEnv.put( JDBCJNDIDataStoreFactory.JNDI_REFNAME.getName(), Fetcher.SEDMAP_DS);
		DataStore store =  DataStoreFinder.getDataStore(dataStoreEnv);

		Filter filter     = OgcUtils.ogcXml2Filter(ogc_v1_0);
		Query query       = new Query("TABLENAME", filter);
		// the no-arg constructor interrogates the call stack for a handle
		Transaction trans = new DefaultTransaction("test handle");
		JDBCFeatureReader reader = (JDBCFeatureReader) store.getFeatureReader(query, trans);
		StringBuilder buf = new StringBuilder();
		while (reader.hasNext()) {
			SimpleFeature feature = reader.next();

			int attribCount = feature.getAttributeCount();
			int expectedCount = 4;
			assertEquals(expectedCount, attribCount);

			List<Object> values = feature.getAttributes();
			Collection<Property> props = feature.getProperties();
			Iterator<Property> iterator = props.iterator();

			for (int a=0; a<4; a++) {
				Property prop = iterator.next();
				buf.append(prop.getName());
				buf.append(":");

				// String attr = feature.getAttribute(a).toString();
				String attr = values.get(a).toString();
				buf.append(attr);
				buf.append(", ");
			}
			buf.append(IoUtils.LINE_SEPARATOR);

		}
		System.out.println(buf);

		assertTrue("expect to find site id 1",buf.indexOf("1234567891")>-1);
		assertTrue("expect to find site id 2",buf.indexOf("1234567892")>-1);
		assertTrue("expect to find site id 3",buf.indexOf("1234567893")>-1);
		assertEquals("expect three rows of data", 3, StrUtils.occurrences("Site_Id", buf.toString()));
	}


	@Test
	public void test_ogcXml2Filter_simple_v1_0() {
		Filter filter = OgcUtils.ogcXml2Filter(ogc_v1_0);

		String expected = "[ Site_Id >= 5 ]";
		String actual   = filter.toString();

		assertEquals("Testing simple filter parse", expected, actual);
	}

	@Test
	public void test_ogcXml2Sql_simple_v1_0() {
		String expected = "WHERE \"Site_Id\" >= ?";
		String actual   = OgcUtils.ogcXml2Sql(ogc_v1_0);

		assertEquals("Testing conversion from toString to SQL", expected, actual);
	}

	@Test
	public void test_ogcXml2Sql_complex_v1_1() {
		String sql = "Parse Failed";
		try {
			sql = OgcUtils.ogcXml2Sql(ogc_v1_1);
		} catch (Exception e) {
			fail("Requires OGC Filter v1.1 parsing to pass. " + e.getMessage());
		}

		System.out.println( sql );
	}

	@Test
	public void test_ogcXml2Sql_complex_v1_1_numeric() throws Exception {
		String sql = OgcUtils.ogcXml2Sql(ogc_v1_1);
		String expect = "\"SAMPLE_YEARS\" = ?";
		assertEquals("expect ["+expect+"] to be present", 1, StrUtils.occurrences(expect, sql) );
	}

	@Test
	public void test_ogcXml2Sql_complex_v1_1_string() throws Exception {
		String sql = OgcUtils.ogcXml2Sql(ogc_v1_1);
		String expect = "\"STATE\" = ?";
		assertEquals("expect ["+expect+"] to be present", 2, StrUtils.occurrences(expect, sql) );
	}

	@Test
	public void test_ogcXml2Sql_complex_v1_1_boolean() throws Exception {
		String sql = OgcUtils.ogcXml2Sql(ogc_v1_1);
		String expect = "\"REFERENCE_SITE\" = ?";
		assertEquals("expect ["+expect+"] to be present", 1, StrUtils.occurrences(expect, sql) );
	}

	@Test
	public void test_ogcXml2Sql_complex_v1_1_or() throws Exception {
		String sql = OgcUtils.ogcXml2Sql(ogc_v1_1);
		String expect = "(\"STATE\" = ? OR \"STATE\" = ?)";
		assertEquals("expect ["+expect+"] to be present", 1, StrUtils.occurrences(expect, sql) );
	}

	@Test
	public void test_ogcXml2Sql_complex_v1_1_numericString() throws Exception {
		String sql = OgcUtils.ogcXml2Sql(ogc_v1_1);
		String expect = "\"GAGE_BASIN_ID\" = ?";
		assertEquals("expect ["+expect+"] to be present", 1, StrUtils.occurrences(expect, sql) );
	}

	@Test
	public void test_invalidChars_none() {
		String qry = "[ foo=bar ]";
		try {
			OgcUtils.checkForInvalidChars(qry);
		} catch (Exception e) {
			fail("No invalid chars should not throw exception. " + e.getMessage());
		}
	}


	@Test(expected=RuntimeException.class)
	public void test_invalidChars_openParentheses() {
		String qry = "[ foo=( ]";

		OgcUtils.checkForInvalidChars(qry);
	}
	@Test(expected=RuntimeException.class)
	public void test_invalidChars_closedParentheses() {
		String qry = "[ foo=) ]";

		OgcUtils.checkForInvalidChars(qry);
	}
	@Test(expected=RuntimeException.class)
	public void test_invalidChars_semicolon() {
		String qry = "[ foo=dfsdf; ]";

		OgcUtils.checkForInvalidChars(qry);
	}


	@Test
	public void test_sqlTranslation_Brakets2parentheses() {
		String query  = "[ ]";
		String expect = "( )";
		String actual = OgcUtils.sqlTranslation(query);

		assertEquals("square brackets replaced with parentheses", expect, actual);
	}

	@Test
	public void test_sqlTranslation_isLike2like() {
		String query  = " is like ";
		String expect = " like ";
		String actual = OgcUtils.sqlTranslation(query);

		assertEquals("remove 'is' from 'is like'", expect, actual);
	}


	@Test
	public void test_sqlTranslation_wildcard2string() {
		String query  = " bar* ";
		String expect = " 'bar%' ";
		String actual = OgcUtils.sqlTranslation(query);

		assertEquals("convert wildcard from bar* to 'bar%' ", expect, actual);
	}

	@Test
	public void test_sqlTranslation_fieldTranslations() {
		Map<String, String> FIELD_TRANSLATIONS = new HashMap<String, String>();
		FIELD_TRANSLATIONS.put("HUC_8","HUC_12");

		String query  = "((( DA >= 10 ) AND ( DA <= 40 )) AND ( HUC_8 like '03%' ))";
		String expect = "((( DA >= 10 ) AND ( DA <= 40 )) AND ( HUC_12 like '03%' ))";
		String actual = OgcUtils.sqlTranslation(query, FIELD_TRANSLATIONS);

		assertEquals("convert wildcard from bar* to 'bar%' ", expect, actual);
	}




}
